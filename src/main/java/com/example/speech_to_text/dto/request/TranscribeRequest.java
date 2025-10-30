package com.example.speech_to_text.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TranscribeRequest {
    private MultipartFile file;
    private String language;
    private String model;
}
