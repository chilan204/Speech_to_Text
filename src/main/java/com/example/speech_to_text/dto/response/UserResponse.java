package com.example.speech_to_text.dto.response;

import com.example.speech_to_text.enums.UserRole;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String emCode;
    private UserRole role;
}
