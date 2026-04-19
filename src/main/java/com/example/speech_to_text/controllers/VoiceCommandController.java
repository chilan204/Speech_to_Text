package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.repositories.UserRepository;
import com.example.speech_to_text.services.AIService;
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

    public VoiceCommandController(
            AIService aiService,
            ObjectMapper mapper,
            UserRepository userRepository,
            UserSessionService sessionService
    ) {
        this.aiService = aiService;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<ResponseBase<JsonNode>> handle(@RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseBase.<JsonNode>builder()
                            .message("Invalid file")
                            .build()
            );
        }

        try (InputStream is = file.getInputStream()) {

            String json = aiService.processVoice(is);

            if (json == null || json.isBlank()) {
                throw new RuntimeException("Empty AI response");
            }

            JsonNode node = mapper.readTree(json);

            if (node.path("status").asText().equals("denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ResponseBase.<JsonNode>builder()
                                .data(node)
                                .message("Unauthorized")
                                .build()
                );
            }

            User user = getCurrentUser();

            if (user != null) {
                sessionService.createFromAIResponse(user, node);
            }

            return ResponseEntity.ok(
                    ResponseBase.<JsonNode>builder()
                            .data(node)
                            .message("Success")
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ResponseBase.<JsonNode>builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}