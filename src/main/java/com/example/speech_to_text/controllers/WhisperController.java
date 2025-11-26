package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.request.TranscribeRequest;
import com.example.speech_to_text.dto.response.TranscribeResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.repositories.UserRepository;
import com.example.speech_to_text.services.UserSessionService;
import com.example.speech_to_text.services.WhisperService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/api/whisper")
public class WhisperController {
    private final WhisperService whisperService;
    private final UserSessionService userSessionService;
    private final UserRepository userRepository;

    @Autowired
    public WhisperController(WhisperService whisperService, UserSessionService userSessionService, UserRepository userRepository) {
        this.whisperService = whisperService;
        this.userSessionService = userSessionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/transcribe")
    public ResponseEntity<ResponseBase<TranscribeResponse>> transcribe(@ModelAttribute TranscribeRequest request) {
        if (request == null || request.getFile() == null || request.getFile().isEmpty()) {
            ResponseBase<TranscribeResponse> resp = ResponseBase.<TranscribeResponse>builder()
                    .data(null)
                    .message("File không tồn tại hoặc rỗng")
                    .build();
            return ResponseEntity.badRequest().body(resp);
        }

        try (InputStream is = request.getFile().getInputStream()) {
            String jsonResponse = whisperService.transcribe(is);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonResponse);

            TranscribeResponse response = new TranscribeResponse();
            response.setText(node.path("text").asText(""));
            response.setLanguage(node.path("language").asText(""));
            response.setStatus(true);

            User user = getCurrentUserOrFallback();
            if (user != null) {
                userSessionService.createUserSession(user, node.path("text").asText(""));
            }

            ResponseBase<TranscribeResponse> res = ResponseBase.<TranscribeResponse>builder()
                    .data(response)
                    .message("Transcribe successfully")
                    .build();
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            ResponseBase<TranscribeResponse> errorResp = ResponseBase.<TranscribeResponse>builder()
                    .data(null)
                    .message("WHISPER_ERROR: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResp);
        }
    }

    private User getCurrentUserOrFallback() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) auth.getPrincipal()).getUsername();
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }
}