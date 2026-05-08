package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.response.VoiceCommandResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.enums.CommandArbitrationStatus;
import com.example.speech_to_text.repositories.UserRepository;
import com.example.speech_to_text.services.AIService;
import com.example.speech_to_text.services.CommandArbitrationService;
import com.example.speech_to_text.services.UserSessionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/voice-command")
public class VoiceCommandController {

    private final AIService aiService;
    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final UserSessionService sessionService;
    private final CommandArbitrationService arbitrationService;

    public VoiceCommandController(
            AIService aiService,
            ObjectMapper mapper,
            UserRepository userRepository,
            UserSessionService sessionService,
            CommandArbitrationService arbitrationService
    ) {
        this.aiService = aiService;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.arbitrationService = arbitrationService;
    }

    @PostMapping
    public ResponseEntity<ResponseBase<VoiceCommandResponse>> handle(
            @RequestParam("file") MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseBase.<VoiceCommandResponse>builder()
                            .message("Invalid file")
                            .build()
            );
        }

        try (InputStream is = file.getInputStream()) {

            User user = getCurrentUser();

            if (user == null) {
                return ResponseEntity.status(401).body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .message("User not found")
                                .build()
                );
            }

            // =========================
            // 1. ARBITRATION FIRST
            // =========================
            CommandArbitrationStatus decision = arbitrationService.processCommand(user);

            // Chỉ lệnh được đánh EXECUTED mới được đi tiếp.
            if (decision != CommandArbitrationStatus.EXECUTED) {

                VoiceCommandResponse reject = new VoiceCommandResponse();
                reject.setStatus(decision.name());
                reject.setRole(user.getRole().name());

                return ResponseEntity.status(403).body(
                        ResponseBase.<VoiceCommandResponse>builder()
                                .data(reject)
                                .message("Rejected by arbitration")
                                .build()
                );
            }

            // =========================
            // 2. AI ONLY IF ALLOWED
            // =========================
            String json = aiService.processVoice(is);

            if (json == null || json.isBlank()) {
                throw new RuntimeException("Empty AI response");
            }

            JsonNode node = mapper.readTree(json);

            VoiceCommandResponse response =
                    mapper.treeToValue(node, VoiceCommandResponse.class);

            // =========================
            // 3. APPLY ARBITRATION RESULT
            // =========================
            response.setStatus(decision.name());
            response.setRole(user.getRole().name());

            // =========================
            // 4. SAVE SESSION
            // =========================
            sessionService.createFromAIResponse(user, response);

            return ResponseEntity.ok(
                    ResponseBase.<VoiceCommandResponse>builder()
                            .data(response)
                            .message("Success")
                            .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                    ResponseBase.<VoiceCommandResponse>builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    private User getCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByUsername(username)
                .orElse(null);
    }
}