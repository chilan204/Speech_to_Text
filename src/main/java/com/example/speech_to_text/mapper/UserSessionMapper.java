package com.example.speech_to_text.mapper;

import com.example.speech_to_text.dto.response.UserSessionResponse;
import com.example.speech_to_text.entities.UserSession;
import org.springframework.stereotype.Component;

@Component
public class UserSessionMapper {
        public UserSessionResponse toResponseDTO(UserSession entity) {
        UserSessionResponse dto = new UserSessionResponse();
        dto.setId(entity.getId());
        dto.setUser_id(entity.getUser().getId());
        dto.setContent(entity.getContent());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
}