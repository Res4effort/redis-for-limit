package com.example.limit.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisUtil {

    private static String keyPattern_1 = "ACCESS:{prefix}:{key}";
    private static String keyPattern_2 = "LIMITED:{prefix}:{key}";

    private static StringRedisTemplate stringRedisCacheTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    private void init() {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        stringRedisTemplate.setKeySerializer(stringSerializer);
        stringRedisTemplate.setValueSerializer(stringSerializer);
        stringRedisTemplate.setHashKeySerializer(stringSerializer);
        stringRedisTemplate.setHashValueSerializer(stringSerializer);

        keyPattern_1 = keyPattern_1.replace("{prefix}", "IPs");
        keyPattern_2 = keyPattern_2.replace("{prefix}", "IPs");
        stringRedisCacheTemplate = this.stringRedisTemplate;
    }

    private static String getKey(String key, boolean isLimited) {
        if (!isLimited) {
            return key == null ? null : keyPattern_1.replace("{key}", key);
        }
        return key == null ? null : keyPattern_2.replace("{key}", key);
    }

    public static boolean set(String key, Object data, long overtime, TimeUnit timeUnit, boolean isLimited) {
        try {
            key = getKey(key, isLimited);
            String jsonData = JSON.toJSONString(data);
            stringRedisCacheTemplate.opsForValue().set(key, jsonData, overtime, timeUnit);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean set(String key, Object data, boolean isLimited) {
        try {
            key = getKey(key, isLimited);
            String jsonData = JSON.toJSONString(data);
            stringRedisCacheTemplate.opsForValue().set(key, jsonData);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static <T> T get(String key, Class<T> tClass, boolean isLimited) {
        try {
            key = getKey(key, isLimited);
            Object dataObj = stringRedisCacheTemplate.opsForValue().get(key);
            return JSON.parseObject(dataObj.toString(), tClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean hasKey(String key, boolean isLimited) {
        key = getKey(key, isLimited);
        return stringRedisCacheTemplate.hasKey(key);
    }

    public static Long getExpire(String key, boolean isLimited) {
        key = getKey(key, isLimited);
        return stringRedisCacheTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
