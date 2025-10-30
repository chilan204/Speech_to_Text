package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.mapper.UserMapper;
import com.example.speech_to_text.repositories.UserRepository;
import com.example.speech_to_text.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
//    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper/*, PasswordEncoder passwordEncoder*/) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
//        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> getAllUser() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDTO)
                .orElse(null) ;
    }

    @Override
    public UserResponse createUser(UserRequest userDTO) {
        User user = userMapper.toEntity(userDTO);
//        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest updatedUserDTO) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(updatedUserDTO.getUsername());
            user.setEmail(updatedUserDTO.getEmail());
            user.setEmCode(updatedUserDTO.getEmCode());
            User updatedUser = userRepository.save(user);
            return userMapper.toResponseDTO(updatedUser);
        }
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
