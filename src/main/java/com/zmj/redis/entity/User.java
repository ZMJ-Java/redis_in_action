package com.zmj.redis.entity;

public class User {

    public static final String USER_ARTICLE_SET_KEY_PREFIX = "user:";

    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名
     */
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
