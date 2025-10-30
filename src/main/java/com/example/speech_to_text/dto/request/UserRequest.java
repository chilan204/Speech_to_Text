package com.example.speech_to_text.dto.request;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private String phone;
    private String emCode;
}
