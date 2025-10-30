package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.request.TranscribeRequest;
import com.example.speech_to_text.dto.response.TranscribeResponse;
import com.example.speech_to_text.dto.response.common.ErrorResponseDTO;
import com.example.speech_to_text.services.WhisperService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/whisper")
public class WhisperController {

    private final WhisperService whisperService;

    @Autowired
    public WhisperController(WhisperService whisperService) {
        this.whisperService = whisperService;
    }

    @PostMapping("/transcribe")
    public ResponseEntity<?> transcribe(@ModelAttribute TranscribeRequest request) {
        try {
            String jsonResponse = whisperService.transcribe(request.getFile().getInputStream());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonResponse);

            TranscribeResponse response = new TranscribeResponse();
            response.setText(node.get("text").asText());
            response.setLanguage(node.get("language").asText());
            response.setModel(node.get("model").asText());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO();
            error.setApiPath(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
            error.setCode("WHISPER_ERROR");
            error.setMessage(e.getMessage());
            error.setErrorTime(LocalDateTime.now());
            error.setObjectErrorRes(null);

            return ResponseEntity.status(500).body(error);
        }
    }
}