package com.example.speech_to_text.services;

import com.example.speech_to_text.dto.response.UserSessionResponse;
import com.example.speech_to_text.dto.response.VoiceCommandResponse;
import com.example.speech_to_text.entities.User;

import java.util.List;

public interface UserSessionService {
    List<UserSessionResponse> getAllUserSession();

    UserSessionResponse getUserSessionById(Long id);

    List<UserSessionResponse> getUserSessionByUserId(Long userId);

    void createFromAIResponse(User user, VoiceCommandResponse response);

    void deleteUserSession(Long id);
}