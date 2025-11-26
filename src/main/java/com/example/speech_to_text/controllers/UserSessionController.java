package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.common.response.ResponseBaseList;
import com.example.speech_to_text.dto.response.UserSessionResponse;
import com.example.speech_to_text.services.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-session")
public class UserSessionController {
    private final UserSessionService userSessionService;

    @Autowired
    public UserSessionController(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @GetMapping
    public ResponseEntity<ResponseBaseList<UserSessionResponse>> getAllUserSession() {
        List<UserSessionResponse> list = userSessionService.getAllUserSession();
        ResponseBaseList<UserSessionResponse> response = ResponseBaseList.<UserSessionResponse>builder()
                .data(list)
                .message("Get UserSession list successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<UserSessionResponse>> getUserSessionById(@PathVariable Long id) {
        UserSessionResponse dto = userSessionService.getUserSessionById(id);
        ResponseBase<UserSessionResponse> response = ResponseBase.<UserSessionResponse>builder()
                .data(dto)
                .message("Get UserSession successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseBaseList<UserSessionResponse>> getMySessions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        List<UserSessionResponse> list = userSessionService.getUserSessionByUsername(username);
        ResponseBaseList<UserSessionResponse> response = ResponseBaseList.<UserSessionResponse>builder()
                .data(list)
                .message("Get my UserSession list successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBase<Void>> deleteUserSession(@PathVariable Long id) {
        userSessionService.deleteUserSession(id);
        ResponseBase<Void> response = ResponseBase.<Void>builder()
                .data(null)
                .message("Delete UserSession successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}