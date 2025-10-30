package com.example.speech_to_text.services;

import java.io.File;
import java.io.InputStream;

public interface WhisperService {
    String transcribe(File audioFile);
    String transcribe(InputStream inputStream);
}