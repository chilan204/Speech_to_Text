package com.example.speech_to_text.services.impl;

import com.example.speech_to_text.services.WhisperService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;

@Service
public class WhisperServiceImpl implements WhisperService {

    private final WebClient webClient;

    public WhisperServiceImpl(
            WebClient.Builder webClientBuilder,
            @Value("${app.whisper.url:http://127.0.0.1:5001}") String baseUrl,
            @Value("${app.whisper.timeout-seconds:120}") long timeoutSeconds) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeoutSeconds))
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutSeconds * 1000);

        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Override
    public String transcribe(File audioFile) {
        try {
            return webClient.post()
                    .uri("/transcribe")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", new FileSystemResource(audioFile)))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(120));
        } catch (Exception e) {
            return "Lỗi khi gửi request đến Whisper server: " + e.getMessage();
        }
    }

    @Override
    public String transcribe(InputStream inputStream) {
        try {
            byte[] bytes = inputStream.readAllBytes();
            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return "audio.wav";
                }
            };

            return webClient.post()
                    .uri("/transcribe")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", resource))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(120));

        } catch (Exception e) {
            return "Lỗi khi gửi request đến Whisper server: " + e.getMessage();
        }
    }
}
