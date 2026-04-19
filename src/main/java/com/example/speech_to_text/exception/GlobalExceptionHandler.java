package com.example.speech_to_text.exception;

import com.example.speech_to_text.dto.common.response.ResponseBase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseBase<Object>> handleRuntime(RuntimeException e) {
        return ResponseEntity.badRequest().body(
                ResponseBase.builder()
                        .message(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBase<Object>> handleException(Exception e) {
        return ResponseEntity.internalServerError().body(
                ResponseBase.builder()
                        .message("Internal Server Error")
                        .build()
        );
    }
}