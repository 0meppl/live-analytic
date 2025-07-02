package com.example.realtimedashboard.service;

import com.example.realtimedashboard.dto.DataPoint;
import com.example.realtimedashboard.handler.DataWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DataGenerationServiceTest {

    private DataWebSocketHandler mockWebSocketHandler;
    private ObjectMapper objectMapper; // Real ObjectMapper is fine for this test
    private DataGenerationService dataGenerationService;

    @BeforeEach
    void setUp() {
        mockWebSocketHandler = Mockito.mock(DataWebSocketHandler.class);
        objectMapper = new ObjectMapper(); // Use a real ObjectMapper for serialization testing
        dataGenerationService = new DataGenerationService(mockWebSocketHandler, objectMapper);
    }

    @Test
    void testGenerateAndBroadcastData_sendsDataPoint() throws Exception {
        dataGenerationService.generateAndBroadcastData();

        // Capture the argument sent to webSocketHandler.broadcast
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockWebSocketHandler, times(1)).broadcast(captor.capture());

        String broadcastedJson = captor.getValue();
        assertNotNull(broadcastedJson);

        // Deserialize and check content
        DataPoint broadcastedDataPoint = objectMapper.readValue(broadcastedJson, DataPoint.class);
        assertNotNull(broadcastedDataPoint);
        assertNotNull(broadcastedDataPoint.getLabel());
        assertTrue(broadcastedDataPoint.getValue() >= 10 && broadcastedDataPoint.getValue() <= 30);

        // Check if lastDataPoint was updated
        DataPoint lastDataPoint = dataGenerationService.getLatestDataPoint();
        assertNotNull(lastDataPoint);
        assertEquals(broadcastedDataPoint.getLabel(), lastDataPoint.getLabel());
        assertEquals(broadcastedDataPoint.getValue(), lastDataPoint.getValue(), 0.001);
    }

    @Test
    void testGetLatestDataPoint_isNullInitially() {
        // Before any generation, lastDataPoint should be null
        assertNull(dataGenerationService.getLatestDataPoint());
    }

    @Test
    void testGetLatestDataPoint_returnsLastGenerated() {
        assertNull(dataGenerationService.getLatestDataPoint());
        dataGenerationService.generateAndBroadcastData();
        DataPoint dp1 = dataGenerationService.getLatestDataPoint();
        assertNotNull(dp1);

        dataGenerationService.generateAndBroadcastData();
        DataPoint dp2 = dataGenerationService.getLatestDataPoint();
        assertNotNull(dp2);
        assertNotEquals(dp1.getLabel(), dp2.getLabel()); // Assuming time changes
    }

    @Test
    void testGetCurrentUserCount_returnsValueInRange() {
        for (int i = 0; i < 100; i++) { // Test a few times due to randomness
            int userCount = dataGenerationService.getCurrentUserCount();
            assertTrue(userCount >= 5 && userCount < 15, "User count " + userCount + " out of range");
        }
    }
}
