package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.enums.CommandArbitrationStatus;
import com.example.speech_to_text.services.CommandArbitrationService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
public class CommandArbitrationServiceImpl implements CommandArbitrationService {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY = "uav:arb:commands";
    private static final long WINDOW = 2000; // ms

    public CommandArbitrationServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public CommandArbitrationStatus processCommand(User user) {
        long submittedAt = System.currentTimeMillis();
        int myLevel = user.getRole().getLevel();
        String member = user.getId() + ":" + myLevel + ":" + UUID.randomUUID();

        redisTemplate.opsForZSet().add(KEY, member, submittedAt);
        redisTemplate.expire(KEY, Duration.ofMillis(WINDOW * 3));

        // Chờ thêm WINDOW để thu thập các lệnh đến ngay sau lệnh hiện tại.
        try {
            Thread.sleep(WINDOW);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Command arbitration interrupted", e);
        }

        long from = submittedAt - WINDOW;
        long to = submittedAt + WINDOW;
        Set<String> participants = redisTemplate.opsForZSet().rangeByScore(KEY, from, to);
        if (participants == null || participants.isEmpty()) {
            return CommandArbitrationStatus.REJECTED_LOW_PRIORITY;
        }

        int maxLevel = Integer.MIN_VALUE;
        int maxLevelCount = 0;

        for (String participant : participants) {
            String[] parts = participant.split(":");
            int level = Integer.parseInt(parts[1]);
            if (level > maxLevel) {
                maxLevel = level;
                maxLevelCount = 1;
            } else if (level == maxLevel) {
                maxLevelCount++;
            }
        }

        if (myLevel < maxLevel) {
            return CommandArbitrationStatus.REJECTED_LOW_PRIORITY;
        }
        if (maxLevelCount > 1) {
            return CommandArbitrationStatus.CONFLICT_SAME_ROLE;
        }

        // Dọn dữ liệu quá cũ để key không phình theo thời gian.
        redisTemplate.opsForZSet().removeRangeByScore(KEY, 0, submittedAt - (WINDOW * 3));
        return CommandArbitrationStatus.EXECUTED;
    }
}