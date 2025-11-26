package com.example.speech_to_text.services;

import com.example.speech_to_text.dto.response.UserSessionResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.entities.UserSession;

import java.util.List;

public interface UserSessionService {
    List<UserSessionResponse> getAllUserSession();

    UserSessionResponse getUserSessionById(Long id);

    List<UserSessionResponse> getUserSessionByUsername(String username);

    UserSession createUserSession(User user, String content);

    void deleteUserSession(Long id);
}