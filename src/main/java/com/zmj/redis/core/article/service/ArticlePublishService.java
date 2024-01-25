package com.zmj.redis.core.article.service;

import com.zmj.redis.common.AjaxResult;
import com.zmj.redis.core.article.domain.Article;

/**
 * @author 14864
 */
public interface ArticlePublishService {

    /**
     * 文章发布
     *
     * @param article 文章
     * @return 结果
     */
    AjaxResult articlePublish(Article article);
}
