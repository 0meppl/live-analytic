package com.example.realtimedashboard.service;

import com.example.realtimedashboard.dto.DataPoint;
import com.example.realtimedashboard.handler.DataWebSocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Service
@EnableScheduling // Important to enable scheduling for @Scheduled annotation
public class DataGenerationService {

    private final DataWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper; // For converting DataPoint to JSON string
    private final Random random = new Random();
    private final AtomicReference<DataPoint> lastDataPoint = new AtomicReference<>();

    @Autowired
    public DataGenerationService(DataWebSocketHandler webSocketHandler, ObjectMapper objectMapper) {
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    // Simulate generating data every 2 seconds
    @Scheduled(fixedRate = 2000) // Rate in milliseconds
    public void generateAndBroadcastData() {
        // Simulate some data, e.g., a random temperature reading
        double simulatedValue = 10 + (30 - 10) * random.nextDouble(); // Random value between 10 and 30
        DataPoint dataPoint = new DataPoint(Math.round(simulatedValue * 100.0) / 100.0); // Rounded to 2 decimal places

        lastDataPoint.set(dataPoint); // Store the latest data point

        try {
            String jsonData = objectMapper.writeValueAsString(dataPoint);
            // System.out.println("Broadcasting data: " + jsonData); // Reduce console noise
            webSocketHandler.broadcast(jsonData);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing DataPoint to JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error broadcasting data: " + e.getMessage());
        }
    }

    public DataPoint getLatestDataPoint() {
        return lastDataPoint.get();
    }

    // Simulate a user count for now
    public int getCurrentUserCount() {
        return 5 + random.nextInt(10); // Random user count between 5 and 14
    }
}
