package org.dromara.ai.utils.dd.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;


import java.util.concurrent.TimeUnit;

import static org.dromara.constant.RedisKeyConstants.DINGTALK_ACCESS_TOKEN;


@Repository
public class DingTalkAccessTokenRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String get() {
        return stringRedisTemplate.opsForValue().get(DINGTALK_ACCESS_TOKEN);
    }


    public void set(String token, Long expiresIn){
        stringRedisTemplate.opsForValue().set(DINGTALK_ACCESS_TOKEN, token, expiresIn - 100, TimeUnit.SECONDS);
    }

}
