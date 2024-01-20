package com.zmj.redis.service;

import com.zmj.redis.common.AjaxResult;
import com.zmj.redis.entity.Article;
import com.zmj.redis.entity.User;

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
