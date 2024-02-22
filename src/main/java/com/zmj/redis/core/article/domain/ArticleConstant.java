package com.zmj.redis.core.article.domain;

/**
 * @author 14864
 * @apiNote 文章元素类
 * @date 2024/1/27 19:00
 */
public class ArticleConstant {
    /**
     * votes hashKey
     */
    public static final String ARTICLE_VOTES = "votes";
    /**
     * scores hashKey
     */
    public static final String ARTICLE_SCORES = "scores";

    /**
     * 热点文章最少的投票数量
     */
    public static final Long HOT_ARTICLE_VOTES_NUMS = 200L;
    /**
     * 文章投票截止时间
     */
    public static final Long ARTICLE_VOTES_CUTOFF_TIME = 7 * 24 * 60 * 60L;
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
    public static final String ARTICLE_PUBLISH_TIME_KEY= "article:publish:time";

    /**
     * 文章发布分数队列redis key前缀
     */
    public static final String ARTICLE_PUBLISH_SCORE_KEY = "article:publish:score";

    /**
     * 文章分组集合 redis key前缀
     */
    public static final String ARTICLE_TYPE_GROUP_KEY_PREFIX = "article:group:";


    /**
     * 获取文章信息 缓存key
     */
    public static String getArticleInfoHashKey(Article article) {
        return ArticleConstant.ARTICLE_INFO_HASH_KEY_PREFIX + article.getId();
    }

    /**
     * 获取文章投票用户集合 缓存key
     */
    public static String getArticleVotedSetKey(Article article) {
        return ArticleConstant.ARTICLE_VOTED_SET_KEY_PREFIX + article.getId();
    }

    /**
     * 获取文章发布时间有序集合 缓存key
     */
    public static String getArticlePublishTimeKey() {
        return ArticleConstant.ARTICLE_PUBLISH_TIME_KEY;
    }

    /**
     * 获取文章发布分数有序集合 缓存key
     */
    public static String getArticlePublishScoreKey() {
        return ArticleConstant.ARTICLE_PUBLISH_SCORE_KEY;
    }

    /**
     * 获取文章分组集合 缓存key
     */
    public static String getArticleTypeGroupKey(Article article) {
        String articleType = article.getArticleType();
        return ArticleConstant.ARTICLE_TYPE_GROUP_KEY_PREFIX + articleType;

    }


}
