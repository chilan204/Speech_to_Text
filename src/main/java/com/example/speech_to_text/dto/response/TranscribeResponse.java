package com.example.speech_to_text.dto.response;

import lombok.Data;

@Data
public class TranscribeResponse {
    private String text;
    private String language;
    private boolean status;
}
