package com.zmj.redis.core.article.service;

import com.zmj.redis.common.AjaxResult;
import com.zmj.redis.core.article.domain.Article;

/**
 * @author 14864
 */
public interface ArticleVoteService {
    /**
     * 判断能否投票
     *
     * @param userId    投票用户ID
     * @param article   文章
     * @return 结果
     */
    Boolean canVote(Long userId, Article article);

    /**
     * 给文章投票
     *
     * @param userId    投票用户ID
     * @param article   文章
     * @return 结果
     */
    AjaxResult voteArticle(Long userId, Article article);

    /**
     * 取消投票
     *
     * @param article     文章
     * @param userId      用户ID
     */
    AjaxResult cancelVote(Long userId, Article article);


}
