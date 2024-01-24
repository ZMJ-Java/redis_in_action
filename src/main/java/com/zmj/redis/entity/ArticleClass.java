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
    PROGRAMMING(1000L, "programming"),
    /**
     * 儿童
     */
    CHILDREN(1001L, "children"),
    /**
     * 技术
     */
    TECHNOLOGY(1002L, "technology"),
    /**
     * 科学
     */
    SCIENCE(1003L, "science");

    private String articleType;

    private Long groupId;

    public static final Map<Long,String> ARTICLE_CLASS_MAP = new HashMap<>();

    static {
        for (ArticleClass value : ArticleClass.values()) {
            ARTICLE_CLASS_MAP.put(value.getGroupId(),value.getArticleType());
        }
    }

    private ArticleClass( Long groupId,String articleType) {
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
