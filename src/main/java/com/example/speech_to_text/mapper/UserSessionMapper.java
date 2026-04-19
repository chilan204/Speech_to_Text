package com.example.speech_to_text.mapper;

import com.example.speech_to_text.dto.response.UserSessionResponse;
import com.example.speech_to_text.entities.UserSession;
import org.springframework.stereotype.Component;

@Component
public class UserSessionMapper {

    public UserSessionResponse toResponseDTO(UserSession entity) {
        if (entity == null) return null;

        UserSessionResponse dto = new UserSessionResponse();

        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());

        dto.setTranscript(entity.getTranscript());
        dto.setAction(entity.getAction());
        dto.setDirection(entity.getDirection());
        dto.setValue(entity.getValue());

        dto.setSpeaker(entity.getSpeaker());
        dto.setSpeakerScore(entity.getSpeakerScore());

        dto.setVerificationScore(entity.getVerificationScore());
        dto.setVerified(entity.getVerified());

        dto.setRawResponse(entity.getRawResponse());

        dto.setCreatedDate(entity.getCreatedDate());

        return dto;
    }
}