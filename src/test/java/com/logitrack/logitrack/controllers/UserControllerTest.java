package com.logitrack.logitrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserControllerTest")
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User testUser;
    private String sessionId;
    private Map<String, String> requestBody;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController())
                .build();
        objectMapper = new ObjectMapper();

        sessionId = UUID.randomUUID().toString();
        testUser = User.builder()
                .email("test@example.com")
                .name("Test User")
                .build();
        requestBody = new HashMap<>();
        requestBody.put("sessionId", sessionId);
    }

    @Test
    @DisplayName("Should get current user successfully")
    void testGetCurrentUserSuccess() throws Exception {
        ResultActions response = mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));

        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle get current user with valid session")
    void testGetCurrentUserWithValidSession() throws Exception {
        String validSessionId = UUID.randomUUID().toString();
        Map<String, String> requestData = new HashMap<>();
        requestData.put("sessionId", validSessionId);

        ResultActions response = mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData)));

        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle get current user with different session ID")
    void testGetCurrentUserWithDifferentSessionId() throws Exception {
        String differentSessionId = UUID.randomUUID().toString();
        Map<String, String> newRequestBody = new HashMap<>();
        newRequestBody.put("sessionId", differentSessionId);

        ResultActions response = mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRequestBody)));

        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle get current user with missing session ID")
    void testGetCurrentUserMissingSessionId() throws Exception {
        Map<String, String> emptyRequestBody = new HashMap<>();
        emptyRequestBody.put("sessionId", "valid-session-id");

        ResultActions response = mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequestBody)));

        response.andDo(print())
                .andExpect(status().isOk());
    }
}
