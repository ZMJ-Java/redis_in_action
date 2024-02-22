package com.zmj.redis.core.user;

/**
 * @author ZMJ
 * @des UserConstant
 * @date 2024/2/2
 */
public class UserConstant {

    public static final String USER_ARTICLE_SET_KEY_PREFIX = "user:article:";

    public static String getUserArticleSetKey(Long userId) {
        return UserConstant.USER_ARTICLE_SET_KEY_PREFIX + userId;
    }
}
