package com.example.speech_to_text.services;

import java.io.InputStream;

public interface AIService {
    String processVoice(InputStream inputStream);
}