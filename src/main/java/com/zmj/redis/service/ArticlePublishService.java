package com.zmj.redis.service;

import com.zmj.redis.common.AjaxResult;
import com.zmj.redis.entity.Article;
import com.zmj.redis.entity.User;

public interface ArticlePublishService {

    /**文章发布*/
    AjaxResult articlePublish(Article article);
}
