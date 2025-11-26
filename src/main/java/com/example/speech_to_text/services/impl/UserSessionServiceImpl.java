package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.dto.response.UserSessionResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.entities.UserSession;
import com.example.speech_to_text.mapper.UserSessionMapper;
import com.example.speech_to_text.repositories.UserSessionRepository;
import com.example.speech_to_text.services.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSessionServiceImpl implements UserSessionService {
    private final UserSessionRepository userSessionRepository;
    private final UserSessionMapper userSessionMapper;
    private final UserServiceImpl userService;

    @Autowired
    public UserSessionServiceImpl(UserSessionRepository userSessionRepository, UserSessionMapper userSessionMapper, UserServiceImpl userService) {
        this.userSessionRepository = userSessionRepository;
        this.userSessionMapper = userSessionMapper;
        this.userService = userService;
    }

    @Override
    public List<UserSessionResponse> getAllUserSession() {
        return userSessionRepository.findAll().stream()
                .map(userSessionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserSessionResponse getUserSessionById(Long id) {
        return userSessionRepository.findById(id)
                .map(userSessionMapper::toResponseDTO)
                .orElse(null) ;
    }

    @Override
    public List<UserSessionResponse> getUserSessionByUsername(String username) {
        User user = userService.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserSession> discounts = userSessionRepository.findByUserIdOrderByIdAsc(user.getId());
        return discounts.stream()
                .map(userSessionMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserSession createUserSession(User user, String content) {
        UserSession session = new UserSession();
        session.setUser(user);
        session.setContent(content);
        return userSessionRepository.save(session);
    }

    @Override
    public void deleteUserSession(Long id) {
        userSessionRepository.deleteById(id);
    }
}
