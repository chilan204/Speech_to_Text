package com.example.speech_to_text.services;

import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.enums.CommandArbitrationStatus;

public interface CommandArbitrationService {

    CommandArbitrationStatus processCommand(User user);
}