package com.example.speech_to_text.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSessionResponse {

    private Long id;

    private Long userId;

    private String transcript;

    private String action;

    private String direction;

    private Integer value;

    private String speaker;

    private Double speakerScore;

    private Double verificationScore;

    private Boolean verified;

    private String rawResponse;

    private LocalDateTime createdDate;
}