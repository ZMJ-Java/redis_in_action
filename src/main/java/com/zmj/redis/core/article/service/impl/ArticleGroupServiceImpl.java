package com.zmj.redis.core.article.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zmj.redis.core.article.domain.ArticleConstant;
import com.zmj.redis.core.article.service.ArticleGroupService;
import com.zmj.redis.core.article.domain.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zmj
 * @apiNote 文章分组实现
 * @date 2024/1/22 22:40
 */
@Service
public class ArticleGroupServiceImpl implements ArticleGroupService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleGroupServiceImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ArticleGroupServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void putArticleToGroup(Article article, Long publishTime) {
        //获取缓存key
        String articleTypeGroupKey = ArticleConstant.getArticleTypeGroupKey(article);
        try
        {
            redisTemplate.opsForZSet().add(articleTypeGroupKey, article.getId(), publishTime);
        } catch (Exception e) {
            logger.warn("添加文章[{}]发生异常,未能成功添加到有序集合[{}]", article.getId(), articleTypeGroupKey);
        }

    }

    @Override
    public void removeArticleFromGroup(Article article) {
        //获取缓存key
        String articleTypeGroupKey = ArticleConstant.getArticleTypeGroupKey(article);
        try {
            redisTemplate.opsForZSet().remove(articleTypeGroupKey, article.getId());
        } catch (Exception e) {
            logger.warn("移除文章[{}]发生异常,未能成功移出有序集合[{}]", article.getId(), articleTypeGroupKey);
        }
    }

    @Override
    public List<Article> getTopScoresArticles(Long numberOfArticles, Long pages) {
        //获取key
        String key = ArticleConstant.getArticlePublishScoreKey();
        //按7天之内的文章分数排序,取numberOfArticles个文章
        long offset = (pages - 1) * numberOfArticles;
        long now = Long.MAX_VALUE;
        long from = 0;
        //拿到文章ID集合
        Set<Object> articleIds = redisTemplate.opsForZSet().rangeByScore(key, from, now, offset, numberOfArticles);
        //创建文章列表
        List<Article> articles = new ArrayList<>();
        if (null == articleIds || articleIds.isEmpty()){
            throw new NullPointerException("articleIds is null");
        }
        for (Object articleId : articleIds) {
            Article article = new Article();
            article.setId(((Integer) articleId).longValue());
            //文章对象属性map
            Map<Object, Object> articleInfoMap = redisTemplate.opsForHash().entries(ArticleConstant.getArticleInfoHashKey(article));
            //转为对象
            article = OBJECT_MAPPER.convertValue(articleInfoMap, Article.class);
            articles.add(article);
        }
        return articles;
    }

    @Override
    public List<Article> getLastArticles(Long numberOfArticles, Long pages) {
        //获取key
        String key = ArticleConstant.getArticlePublishTimeKey();
        //按7天之内的文章分数排序,取numberOfArticles个文章
        long offset = (pages - 1) * numberOfArticles;
        long now = Long.MAX_VALUE;
        long from = 0;
        //拿到文章ID集合
        Set<Object> articleIds = redisTemplate.opsForZSet().rangeByScore(key, from, now, offset, numberOfArticles);
        //创建文章列表
        List<Article> articles = new ArrayList<>();
        for (Object articleId : articleIds) {
            Article article = new Article();
            article.setId(((Integer) articleId).longValue());
            //文章对象属性map
            Map<Object, Object> articleInfoMap = redisTemplate.opsForHash().entries(ArticleConstant.getArticleInfoHashKey(article));
            //转为对象
            article = OBJECT_MAPPER.convertValue(articleInfoMap, Article.class);
            articles.add(article);
        }
        return articles;
    }


}
