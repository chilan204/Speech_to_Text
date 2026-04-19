package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.common.response.ResponseBaseList;
import com.example.speech_to_text.dto.response.UserSessionResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.services.UserService;
import com.example.speech_to_text.services.UserSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-session")
public class UserSessionController {

    private final UserSessionService userSessionService;
    private final UserService userService;

    public UserSessionController(UserSessionService userSessionService, UserService userService) {
        this.userSessionService = userSessionService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseBaseList<UserSessionResponse>> getMySessions() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.findEntityByUsername(username);
        Long userId = user.getId();

        List<UserSessionResponse> list = userSessionService.getUserSessionByUserId(userId);

        return ResponseEntity.ok(
                ResponseBaseList.<UserSessionResponse>builder()
                        .data(list)
                        .message("Success")
                        .build()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseBaseList<UserSessionResponse>> getByUserId(@PathVariable Long userId) {

        List<UserSessionResponse> list = userSessionService.getUserSessionByUserId(userId);

        return ResponseEntity.ok(
                ResponseBaseList.<UserSessionResponse>builder()
                        .data(list)
                        .message("Success")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBase<Void>> delete(@PathVariable Long id) {

        userSessionService.deleteUserSession(id);

        return ResponseEntity.ok(
                ResponseBase.<Void>builder()
                        .message("Deleted")
                        .build()
        );
    }
}