package com.example.speech_to_text.services;

import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUser();

    UserResponse getUserById(Long id);

    UserResponse createUser(UserRequest user);

    UserResponse updateUser(Long id, UserRequest updatedUser);

    void deleteUser(Long id);

    Optional<User> findByUsername(String username);

    User findEntityByUsername(String username);

    void saveVoiceSample(Long userId, MultipartFile file);

    List<String> getVoiceSamples(Long userId);

    void deleteVoiceSample(Long userId, String fileName);

    User save(User user);
}
