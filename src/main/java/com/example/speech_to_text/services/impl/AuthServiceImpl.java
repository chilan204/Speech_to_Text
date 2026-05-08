package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.dto.request.ChangePasswordRequest;
import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.request.ValidateOtpRequest;
import com.example.speech_to_text.dto.response.LoginResponse;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.enums.UserRole;
import com.example.speech_to_text.mapper.UserMapper;
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

    public AuthServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse register(UserRequest req) {

        if (userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();

        user.setUsername(req.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPhone(req.getPhone());
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);

        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public LoginResponse login(UserRequest req) {

        User user = userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        if (!passwordEncoder.matches(
                req.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Override
    public void logout() {
        // JWT stateless -> frontend chỉ cần xóa token
    }

    @Override
    public boolean validateEmail(UserRequest req) {

        User user = userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        return req.getEmail().equals(user.getEmail());
    }

    @Override
    public boolean validateOtp(ValidateOtpRequest req) {

        userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        return "1111".equals(req.getOtp());
    }

    @Override
    public void changePasswordForgot(UserRequest req) {

        User user = userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        user.setPassword(
                passwordEncoder.encode(req.getPassword())
        );

        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest req) {

        User user = userRepository.findByUsername(
                req.getUsername().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        if (!passwordEncoder.matches(
                req.getOldPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException(
                    "Old password is incorrect"
            );
        }

        user.setPassword(
                passwordEncoder.encode(req.getNewPassword())
        );

        userRepository.save(user);
    }
}