package com.zmj.redis.service.impl;

import com.zmj.redis.common.AjaxResult;
import com.zmj.redis.entity.Article;
import com.zmj.redis.entity.User;
import com.zmj.redis.service.ArticleVoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 14864
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
        Boolean isAuthor = userId.equals(article.getAuthor());
        if (Boolean.TRUE.equals(isAuthor)) {
            log.info("该用户[{}]是文章[{}]作者,无法投票",userId,article.getId());
            return false;
        }
        //不是作者本人，在判断是否已经投过票
        String articleVotedSetId = Article.getArticleVotedSetKey(article);
        Boolean hasVote = redisTemplate.opsForSet().isMember(articleVotedSetId, userId);
        if (Boolean.TRUE.equals(hasVote)) {
            log.info("该用户[{}]对文章[{}]投过票,无法再次投票",userId,article.getId());
            return false;
        }
        //判断文章是否还存在
        String authorKey = User.getUserArticleSetKey(article.getAuthor());
        Boolean isExist = redisTemplate.opsForSet().isMember(authorKey, article.getId());
        if (Boolean.FALSE.equals(isExist)){
            log.info("文章[{}]已失效或不存在,该用户[{}]无法投票",article.getId(),userId);
            return false;
        }
        return true;
    }

    @Override
    public AjaxResult voteArticle(Long userId, Article article) {
        //先判断可不可以投票
        Boolean canVote = canVote(userId, article);
        String articleInfoKey = Article.getArticleInfoHashKey(article);
        Long voteNum;
        if (!canVote) {
            return AjaxResult.error("您不可投票");
        }
        String articleVotedSetKey = Article.getArticleVotedSetKey(article);
        String userVotedArticleSetKey = User.getUserArticleSetKey(userId);
        String articleScoreQueueKey = Article.getArticlePublishScoreKey(article);
        //可以投票
        //将投票用户ID加入到文章投票集合
        redisTemplate.opsForSet().add(articleVotedSetKey, userId);
        //将文章ID加入到该用户投票文章集合
        redisTemplate.opsForSet().add(userVotedArticleSetKey, article.getId());
        synchronized (Article.class) {
            //文章投票数量
            voteNum = redisTemplate.opsForHash().increment(articleInfoKey, article.getVotes(), 1L);
            //计算文章分数
            redisTemplate.opsForHash().increment(articleInfoKey, article.getScores(), Article.SCORE_PER_VOTE);
            //增加文章分数队列中文章分数
            redisTemplate.opsForZSet().incrementScore(articleScoreQueueKey, article.getAuthor(), Article.SCORE_PER_VOTE);
        }

        return AjaxResult.success(voteNum);
    }

    @Override
    public AjaxResult cancelVote(Long userId, Article article) {
        //判断是否是作者本人
        Boolean isAuthor = userId.equals(article.getAuthor());
        if (Boolean.TRUE.equals(isAuthor)) {
            log.info("该用户[{}]是文章[{}]作者,无法投票",userId,article.getId());
            return AjaxResult.error("您是作者，无法取消投票");
        }
        //判断是否投过票
        String articleVotedQueueId = Article.getArticleVotedSetKey(article);
        Boolean hasVote = redisTemplate.opsForSet().isMember(articleVotedQueueId, userId);
        if (Boolean.FALSE.equals(hasVote)) {
            return AjaxResult.error("您未投票，无法取消投票");
        }
        //取消投票
        String articleInfoKey = Article.getArticleInfoHashKey(article);
        String userVotedArticleKey = User.getUserArticleSetKey(userId);
        String articleScoreQueueKey = Article.getArticlePublishScoreKey(article);
        //移除用户已投票文章集合中文章
        redisTemplate.opsForSet().remove(userVotedArticleKey, article.getId());
        //移除文章投票的用户集合中的用户
        redisTemplate.opsForSet().remove(articleVotedQueueId, userId);
        synchronized (Article.class) {
            //将文章信息中的投票数量减1,分数减1 * SCORE_PER_VOTE
            redisTemplate.opsForHash().increment(articleInfoKey, article.getVotes(), -1L);
            redisTemplate.opsForHash().increment(articleInfoKey, article.getScores(), -1L * Article.SCORE_PER_VOTE);
            //减少文章分数队列中文章分数
            redisTemplate.opsForZSet().incrementScore(articleScoreQueueKey, article.getAuthor(), -1L * Article.SCORE_PER_VOTE);
        }
        return AjaxResult.success("取消投票成功");
    }
}
