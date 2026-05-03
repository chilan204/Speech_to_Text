package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.dto.request.ChangePasswordRequest;
import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.request.ValidateOtpRequest;
import com.example.speech_to_text.dto.response.LoginResponse;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.enums.UserRole;
import com.example.speech_to_text.mapper.UserMapper;
import com.example.speech_to_text.redis.SessionRedisService;
import com.example.speech_to_text.redis.UserSessionRedis;
import com.example.speech_to_text.repositories.UserRepository;
import com.example.speech_to_text.security.JwtUtil;
import com.example.speech_to_text.services.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final SessionRedisService sessionRedisService;

    public AuthServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            SessionRedisService sessionRedisService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.sessionRedisService = sessionRedisService;
    }

    @Override
    public UserResponse register(UserRequest req) {

        if (userRepository.findByUsername(req.getUsername().toLowerCase()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(req.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);

        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public LoginResponse login(UserRequest req) {

        User user = userRepository.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        UserSessionRedis currentSession = sessionRedisService.getGlobalSession();

        UserRole newRole = user.getRole();

        String newToken = jwtUtil.generateToken(user.getUsername(), newRole.name());

        if (currentSession == null) {
            saveGlobalSession(user, newToken);
            return LoginResponse.builder()
                    .token(newToken)
                    .role(newRole.name())
                    .build();
        }

        UserRole currentRole = currentSession.getRole();

        if (newRole.getLevel() < currentRole.getLevel()) {
            throw new RuntimeException("Your role is lower than active session");
        }

        saveGlobalSession(user, newToken);

        return LoginResponse.builder()
                .token(newToken)
                .role(newRole.name())
                .build();
    }

    private void saveGlobalSession(User user, String token) {

        UserSessionRedis session = new UserSessionRedis(
                user.getUsername(),
                token,
                user.getRole(),
                System.currentTimeMillis()
        );

        sessionRedisService.saveGlobalSession(session);
    }

    @Override
    public void logout() {
        sessionRedisService.deleteGlobalSession();
    }

    @Override
    public boolean validateEmail(UserRequest req) {
        User user = userRepository.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return req.getEmail().equals(user.getEmail());
    }

    @Override
    public boolean validateOtp(ValidateOtpRequest req) {
        userRepository.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return "1111".equals(req.getOtp());
    }

    @Override
    public void changePasswordForgot(UserRequest req) {

        User user = userRepository.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest req) {

        User user = userRepository.findByUsername(req.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }
}