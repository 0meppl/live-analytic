package com.example.realtimedashboard.controller;

import com.example.realtimedashboard.dto.DataPoint;
import com.example.realtimedashboard.google.GoogleAssistantRequest;
import com.example.realtimedashboard.google.GoogleAssistantResponse;
import com.example.realtimedashboard.service.DataGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;


@ExtendWith(MockitoExtension.class)
public class GoogleAssistantWebhookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DataGenerationService mockDataGenerationService;

    @InjectMocks
    private GoogleAssistantWebhookController googleAssistantWebhookController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(googleAssistantWebhookController).build();
    }

    private GoogleAssistantRequest createRequestForIntent(String intentName) {
        GoogleAssistantRequest request = new GoogleAssistantRequest();
        GoogleAssistantRequest.Intent intent = new GoogleAssistantRequest.Intent();
        intent.setName(intentName);
        request.setIntent(intent);
        // Add other necessary fields if your controller uses them (e.g., user, session)
        return request;
    }

    @Test
    void testHandleWebhook_MainIntent() throws Exception {
        GoogleAssistantRequest request = createRequestForIntent("actions.intent.MAIN");

        mockMvc.perform(post("/api/google-assistant/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prompt.firstSimple.speech",
                        containsString("Welcome to the Real-time Dashboard assistant!")));
    }

    @Test
    void testHandleWebhook_GetTemperature_DataAvailable() throws Exception {
        GoogleAssistantRequest request = createRequestForIntent("GetTemperature");
        DataPoint mockDataPoint = new DataPoint(25.5); // Time will be set by constructor
        mockDataPoint.setLabel(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));


        when(mockDataGenerationService.getLatestDataPoint()).thenReturn(mockDataPoint);

        mockMvc.perform(post("/api/google-assistant/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prompt.firstSimple.speech",
                        containsString("The current simulated temperature is 25.50 degrees Celsius, recorded at " + mockDataPoint.getLabel())));
    }

    @Test
    void testHandleWebhook_GetTemperature_NoData() throws Exception {
        GoogleAssistantRequest request = createRequestForIntent("GetTemperature");
        when(mockDataGenerationService.getLatestDataPoint()).thenReturn(null);

        mockMvc.perform(post("/api/google-assistant/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prompt.firstSimple.speech",
                        containsString("I don't have any temperature data at the moment.")));
    }

    @Test
    void testHandleWebhook_UserCount() throws Exception {
        GoogleAssistantRequest request = createRequestForIntent("UserCount");
        when(mockDataGenerationService.getCurrentUserCount()).thenReturn(12);

        mockMvc.perform(post("/api/google-assistant/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prompt.firstSimple.speech",
                        containsString("There are currently 12 simulated users online.")));
    }

    @Test
    void testHandleWebhook_UnknownIntent() throws Exception {
        GoogleAssistantRequest request = createRequestForIntent("SomeUnknownIntent");

        mockMvc.perform(post("/api/google-assistant/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prompt.firstSimple.speech",
                        containsString("Sorry, I didn't understand that.")));
    }

     @Test
    void testHandleWebhook_NullIntent() throws Exception {
        GoogleAssistantRequest request = new GoogleAssistantRequest(); // No intent set
        request.setIntent(null);


        mockMvc.perform(post("/api/google-assistant/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prompt.firstSimple.speech",
                        containsString("Sorry, I didn't understand that.")));
    }

    @Test
    void testHandleWebhook_NullIntentName() throws Exception {
        GoogleAssistantRequest request = createRequestForIntent(null); // Intent object exists, but name is null

        mockMvc.perform(post("/api/google-assistant/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prompt.firstSimple.speech",
                        containsString("Sorry, I didn't understand that.")));
    }
}
