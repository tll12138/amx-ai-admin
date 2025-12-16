package org.dromara.ai.utils.dd.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import static org.dromara.constant.RedisKeyConstants.DINGTALK_USER_UNION_ID;


/**
 * @author zrl
 * @date 2024/10/31
 */
@Repository
public class DingTalkUserUnionRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public static String formatKey(String userId) {
        return String.format(DINGTALK_USER_UNION_ID, userId);
    }

    public String get(String userId) {
        String key = formatKey(userId);
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void set(String userId, String token){
        String key = formatKey(userId);
        stringRedisTemplate.opsForValue().set(key, token);
    }
}
