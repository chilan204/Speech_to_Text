package com.example.speech_to_text.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SessionRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GLOBAL_KEY = "SESSION:ACTIVE_USER";

    private static final String USER_KEY_PREFIX = "SESSION:USER:";

    public SessionRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveGlobalSession(UserSessionRedis session) {
        redisTemplate.opsForValue().set(
                GLOBAL_KEY,
                session,
                Duration.ofHours(24)
        );
    }

    public UserSessionRedis getGlobalSession() {
        Object value = redisTemplate.opsForValue().get(GLOBAL_KEY);
        return (UserSessionRedis) value;
    }

    public void deleteGlobalSession() {
        redisTemplate.delete(GLOBAL_KEY);
    }

    public void saveSession(UserSessionRedis session) {
        redisTemplate.opsForValue().set(
                USER_KEY_PREFIX + session.getUsername(),
                session,
                Duration.ofHours(24)
        );
    }

    public UserSessionRedis getSession(String username) {
        Object value = redisTemplate.opsForValue().get(USER_KEY_PREFIX + username);
        return (UserSessionRedis) value;
    }

    public void deleteSession(String username) {
        redisTemplate.delete(USER_KEY_PREFIX + username);
    }
}