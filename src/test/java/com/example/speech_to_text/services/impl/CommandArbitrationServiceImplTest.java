package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.enums.CommandArbitrationStatus;
import com.example.speech_to_text.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandArbitrationServiceImplTest {

    private StringRedisTemplate redisTemplate;
    private ZSetOperations<String, String> zSetOperations;
    private CommandArbitrationServiceImpl service;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        zSetOperations = mock(ZSetOperations.class);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(redisTemplate.expire(anyString(), any())).thenReturn(Boolean.TRUE);
        when(zSetOperations.add(anyString(), anyString(), any(Double.class))).thenReturn(Boolean.TRUE);

        service = new CommandArbitrationServiceImpl(redisTemplate);
    }

    @Test
    void shouldExecuteWhenCurrentUserIsUniqueHighestRole() {
        User currentUser = user(1L, UserRole.ADMIN);
        when(zSetOperations.rangeByScore(anyString(), any(Double.class), any(Double.class)))
                .thenReturn(Set.of("1:3:a", "2:2:b"));

        CommandArbitrationStatus status = service.processCommand(currentUser);

        assertEquals(CommandArbitrationStatus.EXECUTED, status);
    }

    @Test
    void shouldRejectWhenCurrentUserRoleIsLowerThanHighestRole() {
        User currentUser = user(1L, UserRole.EDITOR);
        when(zSetOperations.rangeByScore(anyString(), any(Double.class), any(Double.class)))
                .thenReturn(Set.of("1:2:a", "2:3:b"));

        CommandArbitrationStatus status = service.processCommand(currentUser);

        assertEquals(CommandArbitrationStatus.REJECTED_LOW_PRIORITY, status);
    }

    @Test
    void shouldRejectAllWhenTopRoleHasMultipleUsers() {
        User currentUser = user(1L, UserRole.ADMIN);
        when(zSetOperations.rangeByScore(anyString(), any(Double.class), any(Double.class)))
                .thenReturn(Set.of("1:3:a", "2:3:b", "3:2:c"));

        CommandArbitrationStatus status = service.processCommand(currentUser);

        assertEquals(CommandArbitrationStatus.CONFLICT_SAME_ROLE, status);
    }

    private User user(Long id, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return user;
    }
}
