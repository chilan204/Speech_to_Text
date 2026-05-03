package com.example.speech_to_text.redis;

import com.example.speech_to_text.enums.UserRole;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionRedis {
    private String username;
    private String token;
    private UserRole role;
    private long loginTime;
}