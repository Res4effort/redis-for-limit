package com.example.limit.access.service.impl;

import com.example.limit.access.service.AccessLimitService;
import com.example.limit.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AccessLimitServiceImpl implements AccessLimitService {

    @Override
    public void saveAccessTimes(String key, int duration, int limitTimes) {
        if (limitTimes == -1) {
            return;
        }
        try {
            boolean containsKey = RedisUtil.hasKey(key, false);
            if (containsKey) {
                int accessTimes = RedisUtil.get(key, Integer.class, false) + 1;
                if (limitTimes <= accessTimes) {
                    RedisUtil.set(key, 1, duration, TimeUnit.SECONDS, true);
                }
                Long expire = RedisUtil.getExpire(key, false);
                RedisUtil.set(key, accessTimes, expire, TimeUnit.SECONDS, false);
            } else {
                RedisUtil.set(key, 1, duration, TimeUnit.SECONDS, false);
            }
        } catch (Exception e) {
            log.info("Save access times failed!");
        }
    }

    @Override
    public boolean isLimited(String limitId) {
        return RedisUtil.hasKey(limitId, true);
    }
}
