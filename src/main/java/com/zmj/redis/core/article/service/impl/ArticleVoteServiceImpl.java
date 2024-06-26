package com.zmj.redis.core.article.service.impl;

import com.zmj.redis.core.article.domain.ArticleConstant;
import com.zmj.redis.core.article.service.ArticleVoteService;
import com.zmj.redis.common.AjaxResult;
import com.zmj.redis.core.article.domain.Article;
import com.zmj.redis.core.user.UserConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author zmj
 */
@Service
public class ArticleVoteServiceImpl implements ArticleVoteService {
    public static final Logger log = LoggerFactory.getLogger(ArticleVoteServiceImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ArticleVoteServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Boolean canVote(Long userId, Article article) {
        //先判断投票人是否是作者本人
        Boolean isAuthor = userId.equals(article.getUserId());
        if (Boolean.TRUE.equals(isAuthor)) {
            log.info("该用户[{}]是文章[{}]作者,无法投票", userId, article.getId());
            return false;
        }
        //不是作者本人，在判断是否已经投过票
        String articleVotedSetId = ArticleConstant.getArticleVotedSetKey(article);
        Boolean hasVote = redisTemplate.opsForSet().isMember(articleVotedSetId, userId);
        if (Boolean.TRUE.equals(hasVote)) {
            log.info("该用户[{}]对文章[{}]投过票,无法再次投票", userId, article.getId());
            return false;
        }
        //判断文章是否还存在
        String authorKey = UserConstant.getUserArticleSetKey(article.getUserId());
        Boolean isExist = redisTemplate.opsForSet().isMember(authorKey, article.getId());
        if (Boolean.FALSE.equals(isExist)) {
            log.info("文章[{}]已失效或不存在,该用户[{}]无法投票", article.getId(), userId);
            return false;
        }
        //判断文章是否截止投票
        String articlePublishTimeKey = ArticleConstant.getArticlePublishTimeKey();
        Double articlePublishTime = redisTemplate.opsForZSet().score(articlePublishTimeKey, article.getId());
        if (null == articlePublishTime) {
            articlePublishTime = 0D;
        }
        return !(ArticleConstant.ARTICLE_VOTES_CUTOFF_TIME < System.currentTimeMillis() - articlePublishTime);
    }

    @Override
    @Transactional
    public AjaxResult voteArticle(Long userId, Article article) {
        //先判断可不可以投票
        Boolean canVote = canVote(userId, article);
        String articleInfoKey = ArticleConstant.getArticleInfoHashKey(article);
        if (!canVote) {
            return AjaxResult.error("您不可投票");
        }
        String articleVotedSetKey = ArticleConstant.getArticleVotedSetKey(article);
        String userVotedArticleSetKey = UserConstant.getUserArticleSetKey(userId);
        String articleScoreQueueKey = ArticleConstant.getArticlePublishScoreKey();
        //可以投票
        //将投票用户ID加入到文章投票集合
        redisTemplate.opsForSet().add(articleVotedSetKey, userId);
        //将文章ID加入到该用户投票文章集合
        redisTemplate.opsForSet().add(userVotedArticleSetKey, article.getId());

        synchronized (Article.class) {
            //文章投票数量
            redisTemplate.opsForHash().increment(articleInfoKey, ArticleConstant.ARTICLE_VOTES, 1L);
            //计算文章分数
            redisTemplate.opsForHash().increment(articleInfoKey, ArticleConstant.ARTICLE_SCORES, ArticleConstant.SCORE_PER_VOTE);
            //增加文章分数队列中文章分数
            redisTemplate.opsForZSet().incrementScore(articleScoreQueueKey, article.getId(), ArticleConstant.SCORE_PER_VOTE);
        }

        return AjaxResult.success("投票成功");
    }

    @Override
    @Transactional
    public AjaxResult cancelVote(Long userId, Article article) throws Exception {
        //判断是否是作者本人
        Boolean isAuthor = userId.equals(article.getUserId());
        if (Boolean.TRUE.equals(isAuthor)) {
            log.info("该用户[{}]是文章[{}]作者,无法投票", userId, article.getId());
            return AjaxResult.error("您是作者，无法取消投票");
        }
        //判断是否投过票
        String articleVotedQueueId = ArticleConstant.getArticleVotedSetKey(article);
        Boolean hasVote = redisTemplate.opsForSet().isMember(articleVotedQueueId, userId);
        if (Boolean.FALSE.equals(hasVote)) {
            return AjaxResult.error("您未投票，无法取消投票");
        }
        try {
            //取消投票
            String articleInfoKey = ArticleConstant.getArticleInfoHashKey(article);
            String userVotedArticleKey = UserConstant.getUserArticleSetKey(userId);
            String articleScoreQueueKey = ArticleConstant.getArticlePublishScoreKey();
            boolean removeVotedUser = removeVotedUser(userVotedArticleKey, article, articleVotedQueueId, userId);
            if (!removeVotedUser) {
                return AjaxResult.error("取消投票失败");
            }
            boolean reduceArticleScores = reduceArticleScores(articleInfoKey, articleScoreQueueKey, article);
            if (!reduceArticleScores) {
                return AjaxResult.error("取消投票失败");
            }
        } catch (Exception e) {
            throw new Exception("取消投票失败");
        }
        return AjaxResult.success("取消投票成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean reduceArticleScores(
            String articleInfoKey,
            String articleScoreQueueKey,
            Article article) throws Exception {
        try {
            synchronized (Article.class) {
                //将文章信息中的投票数量减1,分数减1 * SCORE_PER_VOTE
                redisTemplate.opsForHash().increment(articleInfoKey, ArticleConstant.ARTICLE_VOTES, -1L);
                redisTemplate.opsForHash().increment(articleInfoKey, ArticleConstant.ARTICLE_SCORES, -1L * ArticleConstant.SCORE_PER_VOTE);
                //减少文章分数队列中文章分数
                redisTemplate.opsForZSet().incrementScore(articleScoreQueueKey, article.getId(), -1L * ArticleConstant.SCORE_PER_VOTE);
            }
        } catch (Exception e) {
            throw new Exception("文章分数错误");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeVotedUser(
            String userVotedArticleKey,
            Article article,
            String articleVotedQueueId,
            Long userId) throws Exception {
        try {
            //移除用户已投票文章集合中文章
            redisTemplate.opsForSet().remove(userVotedArticleKey, article.getId());
            //移除文章投票的用户集合中的用户
            redisTemplate.opsForSet().remove(articleVotedQueueId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
