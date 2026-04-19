package com.example.speech_to_text.dto.response;

import lombok.Data;

@Data
public class VoiceCommandResponse {

    private String status;

    private String speaker;

    private Double speakerScore;

    private String text;

    private Object command;

    private Double verificationScore;
}
