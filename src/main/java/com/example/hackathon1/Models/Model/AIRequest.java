package com.example.hackathon1.Models.Model;

import java.util.List;

public record AIRequest(
        String model,
        List<com.example.hackathon1.Models.Model.Message> messages
) {}