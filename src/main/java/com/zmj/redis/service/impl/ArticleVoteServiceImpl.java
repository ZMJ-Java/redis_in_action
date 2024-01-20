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
    public Boolean canVote(User user, Article article) {
        //先判断投票人是否是作者本人
        String userKeyId = User.getUserArticleSetKey(user);
        Boolean isAuthor = redisTemplate.opsForSet().isMember(userKeyId, article.getId());
        if (Boolean.TRUE.equals(isAuthor)) {
            return false;
        }
        //不是作者本人，在判断是否已经投过票
        String articleVotedSetId = Article.getArticleVotedSetKey(article);
        Boolean hasVote = redisTemplate.opsForSet().isMember(articleVotedSetId, user.getId());
        return !Boolean.TRUE.equals(hasVote);
    }

    @Override
    public AjaxResult addArticleVotes(User user, Article article) {
        //先判断可不可以投票
        Boolean canVote = canVote(user, article);
        String articleInfoKey = Article.getArticleInfoHashKey(article);
        Long voteNum;
        if (!canVote) {
            return AjaxResult.error("您不可投票");
        }
        String articleVotedSetKey = Article.getArticleVotedSetKey(article);
        String userVotedArticleSetKey = User.getUserArticleSetKey(user);
        String articleScoreQueueKey = Article.getArticlePublishScoreKey(article);
        //可以投票
        //将投票用户ID加入到文章投票集合
        redisTemplate.opsForSet().add(articleVotedSetKey, user.getId());
        //将文章ID加入到该用户投票文章集合
        redisTemplate.opsForSet().add(userVotedArticleSetKey, article.getId());
        synchronized (Article.class) {
            //文章投票数量
            voteNum = redisTemplate.opsForHash().increment(articleInfoKey, article.getVotes(), 1L);
            //计算文章分数
            redisTemplate.opsForHash().increment(articleInfoKey, article.getScores(), Article.SCORE_PER_VOTE);
            //增加文章分数队列中文章分数
            redisTemplate.opsForZSet().incrementScore(articleScoreQueueKey,article.getAuthor(),Article.SCORE_PER_VOTE);
        }

        return AjaxResult.success(voteNum);
    }

    @Override
    public AjaxResult cancelVote(User user, Article article) {
        //判断是否投过票
        String articleVotedQueueId = Article.getArticleVotedSetKey(article);
        Boolean hasVote = redisTemplate.opsForSet().isMember(articleVotedQueueId, user.getId());
        if (Boolean.FALSE.equals(hasVote)) {
            return AjaxResult.error("您未投票，无法取消投票");
        }
        //取消投票
        String articleInfoKey = Article.getArticleInfoHashKey(article);
        String userVotedArticleKey = User.getUserArticleSetKey(user);
        String articleScoreQueueKey = Article.getArticlePublishScoreKey(article);
        //移除用户已投票文章集合中文章
        redisTemplate.opsForSet().remove(userVotedArticleKey, article.getId());
        //移除文章投票的用户集合中的用户
        redisTemplate.opsForSet().remove(articleVotedQueueId, user.getId());
        Long votes;
        synchronized (Article.class) {
            //将文章信息中的投票数量减1,分数减1 * SCORE_PER_VOTE
            votes = redisTemplate.opsForHash().increment(articleInfoKey, article.getVotes(), -1L);
            redisTemplate.opsForHash().increment(articleInfoKey, article.getScores(), -1L * Article.SCORE_PER_VOTE);
            //减少文章分数队列中文章分数
            redisTemplate.opsForZSet().incrementScore(articleScoreQueueKey,article.getAuthor(),-1L * Article.SCORE_PER_VOTE);
        }
        article.setVotes(votes);
        return AjaxResult.success(article);
    }
}
