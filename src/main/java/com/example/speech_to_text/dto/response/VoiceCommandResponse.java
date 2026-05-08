package com.example.speech_to_text.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VoiceCommandResponse {

    private String status;

    private String speaker;

    @JsonProperty("speaker_score")
    private Double speakerScore;

    @JsonProperty("verification_score")
    private Double verificationScore;

    private String text;

    private Object command;

    private String role;
}
