package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.request.ChangePasswordRequest;
import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.request.ValidateOtpRequest;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.security.JwtUtil;
import com.example.speech_to_text.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserService userService,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseBase<UserResponse>> register(@RequestBody UserRequest req) {

        if (userService.findByUsername(req.getUsername().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body(
                    ResponseBase.<UserResponse>builder()
                            .message("Username already taken")
                            .build()
            );
        }

        req.setUsername(req.getUsername().toLowerCase());
        req.setPassword(passwordEncoder.encode(req.getPassword()));

        UserResponse user = userService.createUser(req);

        return ResponseEntity.ok(
                ResponseBase.<UserResponse>builder()
                        .data(user)
                        .message("Register successfully")
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBase<Map<String, String>>> login(@RequestBody UserRequest req) {

        User user = userService.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(
                    ResponseBase.<Map<String, String>>builder()
                            .message("Invalid username or password")
                            .build()
            );
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("role", user.getRole().name());
        data.put("username", user.getUsername());
        data.put("name", user.getName());

        return ResponseEntity.ok(
                ResponseBase.<Map<String, String>>builder()
                        .data(data)
                        .message("Login successfully")
                        .build()
        );
    }

    @PostMapping("/forgot-password/validate-email")
    public ResponseEntity<ResponseBase<Map<String, Object>>> validateEmail(@RequestBody UserRequest req) {

        User user = userService.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean result = req.getEmail().equals(user.getEmail());

        return ResponseEntity.ok(
                ResponseBase.<Map<String, Object>>builder()
                        .data(Map.of("result", result))
                        .message("Validate email successfully")
                        .build()
        );
    }

    @PostMapping("/forgot-password/validate-otp")
    public ResponseEntity<ResponseBase<Map<String, Object>>> validateOtp(
            @RequestBody ValidateOtpRequest req) {

        userService.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Demo only
        boolean result = "1111".equals(req.getOtp());

        return ResponseEntity.ok(
                ResponseBase.<Map<String, Object>>builder()
                        .data(Map.of("result", result))
                        .message("Validate OTP successfully")
                        .build()
        );
    }

    @PostMapping("/forgot-password/change-password")
    public ResponseEntity<ResponseBase<Void>> changePasswordForgot(@RequestBody UserRequest req) {

        User user = userService.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userService.save(user);

        return ResponseEntity.ok(
                ResponseBase.<Void>builder()
                        .message("Change password successfully")
                        .build()
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseBase<Void>> changePassword(
            @RequestBody ChangePasswordRequest req) {

        User user = userService.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(
                    ResponseBase.<Void>builder()
                            .message("Old password is incorrect")
                            .build()
            );
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userService.save(user);

        return ResponseEntity.ok(
                ResponseBase.<Void>builder()
                        .message("Change password successfully")
                        .build()
        );
    }
}