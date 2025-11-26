package com.example.speech_to_text.dto.request;

import lombok.Data;

@Data
public class ValidateOtpRequest {
    private String username;
    private String otp;
}
