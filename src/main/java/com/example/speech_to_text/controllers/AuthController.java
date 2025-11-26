package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.request.ChangePasswordRequest;
import com.example.speech_to_text.dto.request.ValidateOtpRequest;
import com.example.speech_to_text.security.JwtUtil;
import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {
        if (userService.findByUsername(userRequest.getUsername().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        userRequest.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        userRequest.setUsername(userRequest.getUsername().toLowerCase());

        UserResponse newUser = userService.createUser(userRequest);

        ResponseBase<UserResponse> response = ResponseBase.<UserResponse>builder()
                .data(newUser)
                .message("Register successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/account")
    public ResponseEntity<?> login(@RequestBody UserRequest userRequest) {
        User user = userService.findByUsername(userRequest.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println(">>> storedPassword = " + user.getPassword());
        System.out.println(">>> matches('chilan') = " + passwordEncoder.matches("chilan", user.getPassword()));

        if (!passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Map<String, String> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("role", user.getRole().name());
        resp.put("username", user.getUsername());
        resp.put("name", user.getName());

        ResponseBase<Map<String, String>> response = ResponseBase.<Map<String, String>>builder()
                .data(resp)
                .message("Login successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/validate-email")
    public ResponseEntity<?> validateEmail(@RequestBody UserRequest userRequest) {
        User user = userService.findByUsername(userRequest.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> resp = new HashMap<>();
        resp.put("result", userRequest.getEmail().equals(user.getEmail()));

        ResponseBase<Map<String, Object>> response = ResponseBase.<Map<String, Object>>builder()
                .data(resp)
                .message("Validate email successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody ValidateOtpRequest validateOtpRequest) {
        User user = userService.findByUsername(validateOtpRequest.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> resp = new HashMap<>();
        resp.put("result", validateOtpRequest.getOtp().equals("1111"));

        ResponseBase<Map<String, Object>> response = ResponseBase.<Map<String, Object>>builder()
                .data(resp)
                .message("Validate OTP successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/change-password")
    public ResponseEntity<?> changePasswordByForgotPassWord(@RequestBody UserRequest userRequest) {
        User user = userService.findByUsername(userRequest.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        ResponseBase<Void> response = ResponseBase.<Void>builder()
                .data(null)
                .message("Validate OTP successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = userService.findByUsername(changePasswordRequest.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        }

        ResponseBase<Void> response = ResponseBase.<Void>builder()
                .data(null)
                .message("Change password successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}