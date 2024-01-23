package com.zmj.redis.service.impl;

import com.zmj.redis.entity.Article;
import com.zmj.redis.service.ArticleGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 14864
 * @apiNote 文章分组实现
 * @date 2024/1/22 22:40
 */
@Service
public class ArticleGroupServiceImpl implements ArticleGroupService {

    public static final Logger logger = LoggerFactory.getLogger(ArticleGroupServiceImpl.class);

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ArticleGroupServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void putArticleToGroup(Article article,Long publishTime) {
        //获取缓存key
        String articleTypeGroupKey = Article.getArticleTypeGroupKey(article);
        try {
            redisTemplate.opsForZSet().add(articleTypeGroupKey,article.getId(),publishTime);
        }catch (Exception e){
            logger.warn("添加文章[{}]发生异常,未能成功添加到有序集合[{}]", article.getId(),articleTypeGroupKey);
        }

    }

    @Override
    public void removeArticleFromGroup(Article article) {
        //获取缓存key
        String articleTypeGroupKey = Article.getArticleTypeGroupKey(article);
        try {
            redisTemplate.opsForZSet().remove(articleTypeGroupKey, article.getId());
        } catch (Exception e) {
            logger.warn("移除文章[{}]发生异常,未能成功移出有序集合[{}]", article.getId(),articleTypeGroupKey);
        }
    }


}
