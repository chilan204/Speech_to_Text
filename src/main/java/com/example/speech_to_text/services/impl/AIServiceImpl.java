package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.services.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.InputStream;
import java.time.Duration;

@Service
public class AIServiceImpl implements AIService {

    private final WebClient webClient;

    public AIServiceImpl(
            WebClient.Builder builder,
            @Value("${app.ai.url:http://127.0.0.1:5000}") String baseUrl,
            @Value("${app.ai.timeout-seconds:120}") long timeoutSeconds) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeoutSeconds));

        this.webClient = builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Override
    public String processVoice(InputStream inputStream) {
        try {
            byte[] bytes = inputStream.readAllBytes();

            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return "audio.wav";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            return webClient.post()
                    .uri("/voice-command")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("AI service error: " + e.getMessage());
        }
    }
}