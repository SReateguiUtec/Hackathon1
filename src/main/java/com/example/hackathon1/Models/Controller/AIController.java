package com.example.hackathon1.Models.Controller;

import com.example.hackathon1.Models.DTO.AIRequestDTO;
import com.example.hackathon1.Models.DTO.AIResponseDTO;
import com.example.hackathon1.Models.Model.AIResponse;
import com.example.hackathon1.Models.Service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AIResponseDTO> chatWithAI(@RequestBody AIRequestDTO aiRequestDTO) {
        try {
            AIResponseDTO response = aiService.chatWithAI(aiRequestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new AIResponseDTO("Error: " + e.getMessage()));
        }
    }
}
