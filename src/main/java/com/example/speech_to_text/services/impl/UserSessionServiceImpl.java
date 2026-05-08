package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.dto.response.UserSessionResponse;
import com.example.speech_to_text.dto.response.VoiceCommandResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.entities.UserSession;
import com.example.speech_to_text.enums.CommandArbitrationStatus;
import com.example.speech_to_text.mapper.UserSessionMapper;
import com.example.speech_to_text.repositories.UserSessionRepository;
import com.example.speech_to_text.services.UserSessionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository repository;
    private final UserSessionMapper mapper;

    public UserSessionServiceImpl(UserSessionRepository repository, UserSessionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<UserSessionResponse> getAllUserSession() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserSessionResponse getUserSessionById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElse(null);
    }

    @Override
    public List<UserSessionResponse> getUserSessionByUserId(Long userId) {
        return repository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public void createFromAIResponse(User user, VoiceCommandResponse response) {
        UserSession session = UserSession.builder()
                .user(user)
                .transcript(response.getText())
                .speaker(response.getSpeaker())
                .speakerScore(response.getSpeakerScore())
                .verificationScore(response.getVerificationScore())
                .role(response.getRole())
                .commandStatus(response.getStatus())
                .executed(
                        CommandArbitrationStatus.EXECUTED.name()
                                .equals(response.getStatus()))
                .rawResponse(response.toString())
                .build();
        repository.save(session);
    }

    @Override
    public void deleteUserSession(Long id) {
        repository.deleteById(id);
    }
}