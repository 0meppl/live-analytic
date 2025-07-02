package com.example.realtimedashboard.controller;

import com.example.realtimedashboard.dto.DataPoint;
import com.example.realtimedashboard.google.GoogleAssistantRequest;
import com.example.realtimedashboard.google.GoogleAssistantResponse;
import com.example.realtimedashboard.service.DataGenerationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/google-assistant")
public class GoogleAssistantWebhookController {

    private final ObjectMapper objectMapper = new ObjectMapper(); // For logging request
    private final DataGenerationService dataGenerationService;

    @Autowired
    public GoogleAssistantWebhookController(DataGenerationService dataGenerationService) {
        this.dataGenerationService = dataGenerationService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<GoogleAssistantResponse> handleWebhook(
            @RequestBody GoogleAssistantRequest request,
            @RequestHeader Map<String, String> headers) {

        try {
            System.out.println("Received Google Assistant Request Body: " + objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing request to JSON for logging: " + e.getMessage());
        }

        String intentName = (request.getIntent() != null && request.getIntent().getName() != null)
                            ? request.getIntent().getName() : "unknown.intent";

        System.out.println("Processing intent: " + intentName);
        String speechText;

        switch (intentName) {
            case "actions.intent.MAIN":
                speechText = "Welcome to the Real-time Dashboard assistant! You can ask for the current temperature or user count.";
                break;
            case "GetTemperature": // Assuming this is the intent name defined in Actions Console
            case "TemperatureCheck": // Alternative name
            case "GetCurrentValues": // Generic query that might include temperature
                DataPoint latestData = dataGenerationService.getLatestDataPoint();
                if (latestData != null) {
                    speechText = String.format("The current simulated temperature is %.2f degrees Celsius, recorded at %s.",
                                               latestData.getValue(), latestData.getLabel());
                } else {
                    speechText = "I don't have any temperature data at the moment. Please try again soon.";
                }
                break;
            case "UserCount": // Assuming this is the intent name for user count
            case "GetOnlineUsers":
                int userCount = dataGenerationService.getCurrentUserCount();
                speechText = String.format("There are currently %d simulated users online.", userCount);
                break;
            default:
                speechText = "Sorry, I didn't understand that. You can ask for the temperature or how many users are online.";
                break;
        }

        GoogleAssistantResponse response = new GoogleAssistantResponse(speechText);
        return ResponseEntity.ok(response);
    }
}
