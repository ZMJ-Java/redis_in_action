package com.zmj.redis.entity;

import java.io.Serializable;

/**
 * @author 14864
 */
public class User implements Serializable {

    public static final String USER_ARTICLE_SET_KEY_PREFIX = "user:article:";

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

    public static String getUserArticleSetKey(Long userId){
       return User.USER_ARTICLE_SET_KEY_PREFIX + userId;
    }

}
