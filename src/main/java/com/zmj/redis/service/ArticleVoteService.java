package com.zmj.redis.service;

import com.zmj.redis.common.AjaxResult;
import com.zmj.redis.entity.Article;
import com.zmj.redis.entity.User;

/**
 * @author 14864
 */
public interface ArticleVoteService {
    /**
     * 判断能否投票
     *
     * @param user    投票用户
     * @param article 文章
     * @return 结果
     */
    Boolean canVote(User user, Article article);

    /**
     * 给文章投票
     *
     * @param user    投票用户
     * @param article 文章
     * @return 结果
     */
    AjaxResult addArticleVotes(User user, Article article);

    /**
     * 取消投票
     *
     * @param article 文章
     * @param user    用户
     */
    AjaxResult cancelVote(User user, Article article);


}
