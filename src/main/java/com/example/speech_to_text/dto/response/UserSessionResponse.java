package com.example.speech_to_text.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSessionResponse {
    private Long id;
    private Long user_id;
    private String content;
    private LocalDateTime createdDate;
}
