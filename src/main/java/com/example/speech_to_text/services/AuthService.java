package com.example.speech_to_text.services;

import com.example.speech_to_text.dto.request.ChangePasswordRequest;
import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.request.ValidateOtpRequest;
import com.example.speech_to_text.dto.response.LoginResponse;
import com.example.speech_to_text.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(UserRequest req);

    LoginResponse login(UserRequest req);

    void logout();

    boolean validateEmail(UserRequest req);

    boolean validateOtp(ValidateOtpRequest req);

    void changePasswordForgot(UserRequest req);

    void changePassword(ChangePasswordRequest req);
}