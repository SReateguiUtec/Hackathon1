package com.example.hackathon1.Models.Model;

import java.util.List;
import java.util.Map;

public class AIRequest {
    private String model;
    private List<Map<String, String>> messages;

    public AIRequest() {
    }

    public AIRequest(String model, List<Map<String, String>> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public void setMessages(List<Map<String, String>> messages) {
        this.messages = messages;
    }
}
