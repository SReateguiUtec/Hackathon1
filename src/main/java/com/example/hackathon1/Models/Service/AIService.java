package com.example.hackathon1.Models.Service;

import com.example.hackathon1.Models.DTO.AIRequestDTO;
import com.example.hackathon1.Models.DTO.AIResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public AIResponseDTO chatWithAI(AIRequestDTO aiRequestDTO) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-5-mini");
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", aiRequestDTO.getPrompt())
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    return new AIResponseDTO(content.trim());
                }
            }

            return new AIResponseDTO("No se recibió una respuesta válida del modelo.");

        } catch (Exception e) {
            e.printStackTrace();
            return new AIResponseDTO("Error al comunicarse con el modelo: " + e.getMessage());
        }
    }
}
