package com.zmj.redis.service;

import com.zmj.redis.entity.Article;

/**
 * @author 14864
 * @apiNote 文章分组service
 * @date 2024/1/22 22:40
 */
public interface ArticleGroupService {
    /**
     * 文章发布时，判断该文章类型加入到对应分组
     *
     * @param article 文章对象
     * @param publishTime 发布时间
     */
    void putArticleToGroup(Article article , Long publishTime);

    void removeArticleFromGroup(Article article);
}
