package com.zmj.redis.controller;

import com.zmj.redis.common.AjaxResult;
import com.zmj.redis.entity.Article;
import com.zmj.redis.service.ArticleGroupService;
import com.zmj.redis.service.ArticlePublishService;
import com.zmj.redis.service.ArticleVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 14864
 * @apiNote 文章投票controller
 * @date 2024/1/20 13:34
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleVoteService articleVoteService;

    private final ArticlePublishService articlePublishService;

    private final ArticleGroupService articleGroupService;

    @Autowired
    public ArticleController(ArticleVoteService articleVoteService, ArticlePublishService articlePublishService, ArticleGroupService articleGroupService) {
        this.articleVoteService = articleVoteService;
        this.articlePublishService = articlePublishService;
        this.articleGroupService = articleGroupService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/vote")
    public AjaxResult voteArticle(
            @RequestBody Article article,
            @RequestParam(value = "userId", required = true) Long userId) {
        return articleVoteService.voteArticle(userId, article);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/publish")
    public AjaxResult publishArticle(
            @RequestBody Article article) {
        return articlePublishService.articlePublish(article);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/cancelVote")
    public AjaxResult cancelVote(
            @RequestParam(value = "userId", required = true) Long userId,
            @RequestBody Article article) {
        return articleVoteService.cancelVote(userId, article);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/getTopArticles/{nums}")
    public AjaxResult getTopArticles(@PathVariable("nums") Long nums){
        List<Article> topScoresArticle = articleGroupService.getTopScoresArticleId(nums);
        return AjaxResult.success(topScoresArticle);
    }
}
