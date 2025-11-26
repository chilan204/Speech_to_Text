package com.example.speech_to_text;

import com.example.speech_to_text.entities.User;
import com.example.speech_to_text.enums.UserRole;
import com.example.speech_to_text.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@RequiredArgsConstructor
public class SpeechToTextApplication implements CommandLineRunner {

    @Value("${app.init.admin.username:admin}")
    private String adminUsername;

    @Value("${app.init.admin.password:admin}")
    private String adminPassword;

    @Value("${app.init.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.init.admin.name:Administrator}")
    private String adminName;

    @Value("${app.init.admin.phone:000000000}")
    private String adminPhone;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SpeechToTextApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) {
        Optional<User> existing = userRepository.findByUsername(adminUsername);
        if (existing.isEmpty()) {
            if (userRepository.findByEmail(adminEmail).isPresent()) {
                System.out.println("Admin email already present in DB");
                return;
            }

            User admin = new User();
            admin.setUsername(adminUsername.toLowerCase());
            admin.setName(adminName);
            admin.setEmail(adminEmail.toLowerCase());
            admin.setPhone(adminPhone);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setCreatedBy("system");
            admin.setModifiedBy("system");
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
            System.out.println("Default admin created: username=" + adminUsername);
        } else {
            System.out.println("Admin user already exists: " + adminUsername);
        }
    }
}
