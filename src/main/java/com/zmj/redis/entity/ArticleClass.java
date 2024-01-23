package com.zmj.redis.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 14864
 * @apiNote 文章类型枚举
 * @date 2024/1/23 21:20
 */
public enum ArticleClass implements Serializable {

    /**
     * 编程
     */
    PROGRAMMING("programming", 1000L),
    /**
     * 儿童
     */
    CHILDREN("children", 1001L),
    /**
     * 技术
     */
    TECHNOLOGY("technology", 1002L),
    /**
     * 科学
     */
    SCIENCE("science", 1003L);

    private String articleType;

    private Long groupId;

    public static final Map<String, Long> ARTICLE_CLASS_MAP = new HashMap<>();

    static {
        for (ArticleClass value : ArticleClass.values()) {
            ARTICLE_CLASS_MAP.put(value.getArticleType(), value.getGroupId());
        }
    }

    private ArticleClass(String articleType, Long groupId) {
        this.articleType = articleType;
        this.groupId = groupId;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
