package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.request.ChangePasswordRequest;
import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.request.ValidateOtpRequest;
import com.example.speech_to_text.dto.response.LoginResponse;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseBase<UserResponse>> register(@RequestBody UserRequest req) {
        UserResponse res = authService.register(req);

        return ResponseEntity.ok(
                ResponseBase.<UserResponse>builder()
                        .data(res)
                        .message("Register successfully")
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBase<LoginResponse>> login(@RequestBody UserRequest req) {
        LoginResponse res = authService.login(req);

        return ResponseEntity.ok(
                ResponseBase.<LoginResponse>builder()
                        .data(res)
                        .message("Login successfully")
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();

        return ResponseEntity.ok(
                Map.of("message", "Logout successfully")
        );
    }

    @PostMapping("/forgot-password/validate-email")
    public ResponseEntity<ResponseBase<Map<String, Object>>> validateEmail(@RequestBody UserRequest req) {
        boolean result = authService.validateEmail(req);

        return ResponseEntity.ok(
                ResponseBase.<Map<String, Object>>builder()
                        .data(Map.of("result", result))
                        .message("Validate email successfully")
                        .build()
        );
    }

    @PostMapping("/forgot-password/validate-otp")
    public ResponseEntity<ResponseBase<Map<String, Object>>> validateOtp(@RequestBody ValidateOtpRequest req) {
        boolean result = authService.validateOtp(req);

        return ResponseEntity.ok(
                ResponseBase.<Map<String, Object>>builder()
                        .data(Map.of("result", result))
                        .message("Validate OTP successfully")
                        .build()
        );
    }

    @PostMapping("/forgot-password/change-password")
    public ResponseEntity<ResponseBase<Void>> changePasswordForgot(@RequestBody UserRequest req) {
        authService.changePasswordForgot(req);

        return ResponseEntity.ok(
                ResponseBase.<Void>builder()
                        .message("Change password successfully")
                        .build()
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseBase<Void>> changePassword(@RequestBody ChangePasswordRequest req) {
        authService.changePassword(req);

        return ResponseEntity.ok(
                ResponseBase.<Void>builder()
                        .message("Change password successfully")
                        .build()
        );
    }
}