package com.example.speech_to_text.controllers;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import com.example.speech_to_text.dto.common.response.ResponseBaseList;
import com.example.speech_to_text.dto.request.UserRequest;
import com.example.speech_to_text.dto.response.UserResponse;
import com.example.speech_to_text.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponseBaseList<UserResponse>> getAllUser() {
        List<UserResponse> list = userService.getAllUser();
        ResponseBaseList<UserResponse> response = ResponseBaseList.<UserResponse>builder()
                .data(list)
                .message("Get User list successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse dto = userService.getUserById(id);
        ResponseBase<UserResponse> response = ResponseBase.<UserResponse>builder()
                .data(dto)
                .message("Get User successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ResponseBase<UserResponse>> createUser(@RequestBody UserRequest User) {
        UserResponse dto = userService.createUser(User);
        ResponseBase<UserResponse> response = ResponseBase.<UserResponse>builder()
                .data(dto)
                .message("Create User successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseBase<UserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest updateUser) {
        UserResponse dto = userService.updateUser(id, updateUser);
        ResponseBase<UserResponse> response = ResponseBase.<UserResponse>builder()
                .data(dto)
                .message("Put User successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBase<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        ResponseBase<Void> response = ResponseBase.<Void>builder()
                .data(null)
                .message("Delete User successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}