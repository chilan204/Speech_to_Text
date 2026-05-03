package com.example.speech_to_text.dto.response;

import lombok.*;

@Data
@Builder
public class LoginResponse {

    private String token;

    private String role;

    private String username;

    private String name;

    private Long userId;

    private long expiresIn;
}