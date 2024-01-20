package com.zmj.redis.service.impl;

import com.zmj.redis.common.AjaxResult;
import com.zmj.redis.entity.Article;
import com.zmj.redis.entity.User;
import com.zmj.redis.service.ArticlePublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 14864
 * @date 2024/01/20 4:20:00
 */
@Service
public class ArticlePublishServiceImpl implements ArticlePublishService {

    public static final Logger log = LoggerFactory.getLogger(ArticleVoteServiceImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ArticlePublishServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public AjaxResult articlePublish(Article article) {
        long publishTime = System.currentTimeMillis();
        String articleTimeQueueKey = Article.getArticlePublishTimeKey(article);
        String articleVotedKey = Article.getArticleVotedSetKey(article);
        String articleScoreQueueKey = Article.getArticlePublishScoreKey(article);
        String userArticleSetKey = User.getUserArticleSetKey(article.getAuthor());
        //将自身加入到文章投票列表中
        redisTemplate.opsForSet().add(articleVotedKey, article.getAuthor());
        //将文章加入到作者文章集合里
        redisTemplate.opsForSet().add(userArticleSetKey,article.getId());
        //将文章加入到SortSet K:文章ID V:文章发布者 Score:发布时间
        redisTemplate.opsForZSet().add(articleTimeQueueKey, article.getAuthor(), publishTime);
        //将文章加入到SortSet K:文章ID V:文章发布者 Score:文章分数(评分)
        redisTemplate.opsForZSet().add(articleScoreQueueKey, article.getAuthor(), publishTime + Article.SCORE_PER_VOTE);
        return AjaxResult.success("文章发布成功");
    }
}
