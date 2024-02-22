package com.zmj.redis.core.article.domain;

import java.io.Serializable;

/**
 * @author zmj
 */
public class Article implements Serializable {
    /**
     * 文章id
     */
    private Long id;
    /**
     * 文章名称
     */
    private String title;
    /**
     * 文章作者Id
     */
    private Long userId;
    /**
     * 文章链接
     */
    private String link;
    /**
     * 发布时间
     */
    private String publishTime;
    /**
     * 文章投票数
     */
    private Long votes;
    /**
     * 文章分数
     */
    private Long scores;
    /**
     * 文章类别
     */
    private String articleType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public Long getVotes() {
        return votes;
    }

    public void setVotes(Long votes) {
        this.votes = votes;
    }

    public Long getScores() {
        return scores;
    }

    public void setScores(Long scores) {
        this.scores = scores;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

}
