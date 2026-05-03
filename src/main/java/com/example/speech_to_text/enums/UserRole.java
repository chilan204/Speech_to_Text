package com.example.speech_to_text.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    USER(1),
    EDITOR(2),
    ADMIN(3);

    private final int level;

    UserRole(int level) {
        this.level = level;
    }

}