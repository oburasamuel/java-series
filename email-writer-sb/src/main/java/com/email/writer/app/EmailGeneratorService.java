package com.email.writer.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.api.key}")
    private String groqApiKey;

    // Comma-separated models from properties
    @Value("${groq.models}")
    private String groqModels;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailRequest emailRequest) {
        String prompt = buildPrompt(emailRequest);
        List<String> modelList = Arrays.asList(groqModels.split(","));

        Instant startTime = Instant.now();

        // Create async calls for all models
        List<Mono<ModelResponse>> calls = modelList.stream()
                .map(model -> callGroqApiAsync(prompt, model.trim()))
                .toList();

        // Wait for fastest non-empty result
        ModelResponse winner = Mono.firstWithValue(calls)
                .block(Duration.ofSeconds(20));

        if (winner != null && winner.content() != null && !winner.content().isEmpty()) {
            long elapsedMs = Duration.between(startTime, Instant.now()).toMillis();
            System.out.printf("🏆 Fastest model: %s (responded in %d ms)%n",
                    winner.model(), elapsedMs);
            return winner.content();
        }

        return "All Groq models failed to respond.";
    }

    private Mono<ModelResponse> callGroqApiAsync(String prompt, String model) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                }
        );

        return webClient.post()
                .uri(groqApiUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + groqApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> new ModelResponse(model, extractResponseContentSafe(response)))
                .filter(resp -> resp.content() != null && !resp.content().isEmpty())
                .onErrorResume(e -> {
                    System.err.println("❌ Model failed: " + model + " — " + e.getMessage());
                    return Mono.empty();
                });
    }

    private String extractResponseContentSafe(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            return "";
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line. ");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone. ");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }

    // Record type to hold model name and response
    private record ModelResponse(String model, String content) {}
}
