package com.zmj.redis.redis_in_action;

import com.zmj.redis.core.article.domain.ArticleClassEnum;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.Objects;
import java.util.PrimitiveIterator;

@SpringBootTest
class RedisInActionApplicationTests {


    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisInActionApplicationTests(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Test
    void testRedisMaxStore() {
        byte[] bytes = new byte[1024 * 1024 * 256];

        String toPrintable = ClassLayout.parseInstance(bytes).toPrintable();

        System.out.println(1024*1024*100);

        Boolean deleted = redisTemplate.delete("array");

        System.out.println(deleted);


    }

}
