package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.mapper.UserMapper;
import com.example.speech_to_text.repositories.UserRepository;
import com.example.speech_to_text.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final String baseDir;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            @Value("${app.voice.storage:voice_samples}") String baseDir
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.baseDir = baseDir;
        initBaseDir();
    }

    private void initBaseDir() {
        try {
            Files.createDirectories(Paths.get(baseDir));
        } catch (IOException e) {
            throw new RuntimeException("Cannot init voice storage dir");
        }
    }

    @Override
    public List<UserResponse> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponse createUser(UserRequest userDTO) {
        User user = userMapper.toEntity(userDTO);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest updatedUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateEntity(user, updatedUserDTO);

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Path userDir = Paths.get(baseDir, user.getId().toString());

        try {
            if (Files.exists(userDir)) {
                Files.walk(userDir)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(p -> {
                            try { Files.delete(p); }
                            catch (IOException e) { throw new RuntimeException("Delete fail: " + p); }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting user voice folder");
        }

        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void saveVoiceSample(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Path userDir = Paths.get(baseDir, user.getId().toString());
            Files.createDirectories(userDir);

            String original = file.getOriginalFilename();
            String safeName = (original == null || original.isBlank()) ? "audio.wav" : original;

            String fileName = System.currentTimeMillis() + "_" + safeName;
            Path filePath = userDir.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Error saving voice sample");
        }
    }

    @Override
    public List<String> getVoiceSamples(Long userId) {
        Path userDir = Paths.get(baseDir, userId.toString());

        try {
            if (!Files.exists(userDir)) return List.of();

            return Files.list(userDir)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Error reading voice samples");
        }
    }

    @Override
    public void deleteVoiceSample(Long userId, String fileName) {
        Path filePath = Paths.get(baseDir, userId.toString(), fileName);

        try {
            if (!Files.exists(filePath)) throw new RuntimeException("File not found");
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting voice sample");
        }
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}