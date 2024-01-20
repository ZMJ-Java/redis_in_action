package com.zmj.redis.entity;

/**
 * @author 14864
 */
public class Article {
    /**
     * 热点文章最少的投票数量
     */
    public static final Long HOT_ARTICLE_VOTES_NUMS = 200L;
    /**
     * 文章每一票所代表的分数
     */
    public static final Long SCORE_PER_VOTE = (24 * 60 * 60) / HOT_ARTICLE_VOTES_NUMS;
    /**
     * 文章投票用户集合的redis key前缀
     */
    public static final String ARTICLE_VOTED_SET_KEY_PREFIX = "article:voted:user:";

    /**
     * 文章信息的redis key前缀
     */
    public static final String ARTICLE_INFO_HASH_KEY_PREFIX = "article:info:";

    /**
     * 文章发布时间队列redis key前缀
     */
    public static final String ARTICLE_PUBLISH_TIME_KEY_PREFIX = "article:publish:time:";

    /**
     * 文章发布分数队列redis key前缀
     */
    public static final String ARTICLE_PUBLISH_SCORE_KEY_PREFIX = "article:publish:score:";

    /**
     * 文章id
     */
    private Long id;
    /**
     * 文章名称
     */
    private String title;
    /**
     * 文章作者
     */
    private User author;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
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

    /**
     * 获取文章信息 缓存key
     */
    public static String getArticleInfoHashKey(Article article) {
        return Article.ARTICLE_INFO_HASH_KEY_PREFIX + article.getId();
    }

    /**
     * 获取文章投票用户集合 缓存key
     */
    public static String getArticleVotedSetKey(Article article) {
        return Article.ARTICLE_VOTED_SET_KEY_PREFIX + article.getId();
    }

    /**
     * 获取文章发布时间有序集合 缓存key
     */
    public static String getArticlePublishTimeKey(Article article) {
        return Article.ARTICLE_PUBLISH_TIME_KEY_PREFIX + article.getId();
    }

    /**
     * 获取文章发布分数有序集合 缓存key
     */
    public static String getArticlePublishScoreKey(Article article) {
        return Article.ARTICLE_PUBLISH_SCORE_KEY_PREFIX + article.getId();
    }
}
