package com.zmj.redis.controller;

import com.sun.istack.internal.NotNull;
import com.zmj.redis.common.AjaxResult;
import com.zmj.redis.core.user.User;
import com.zmj.redis.core.user.UserConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZMJ
 * @des UserController
 * @date 2024/2/2
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    public UserController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/login")
    public AjaxResult login(@RequestBody @NotNull User user){
        String name = user.getName();
        String password = user.getPassword();
        redisTemplate.opsForValue().get(UserConstant.getUserArticleSetKey(user.getId()));
        return AjaxResult.success();
    }
}