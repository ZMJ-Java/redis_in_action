package com.zmj.redis.service;

import com.zmj.redis.entity.Article;

import java.util.List;

/**
 * @author 14864
 * @apiNote 文章分组service
 * @date 2024/1/22 22:40
 */
public interface ArticleGroupService {
    /**
     * 文章发布时，将文章加入到对应类型分组
     *
     * @param article     文章对象
     * @param publishTime 发布时间
     */
    void putArticleToGroup(Article article, Long publishTime);

    /**
     * 文章过期或删除,将文章移出分组
     *
     * @param article 文章对象
     */
    void removeArticleFromGroup(Article article);


    /**
     * 文章分页,取出分数前numberOfArticles的文章Id
     *
     * @param numberOfArticles 文章数量
     * @return 文章列表
     */
    List<Article> getTopScoresArticleId(Long numberOfArticles);
}
