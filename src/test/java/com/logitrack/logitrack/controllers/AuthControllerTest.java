package com.logitrack.logitrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.models.ENUM.Role;
import com.logitrack.logitrack.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthControllerTest")
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    private UserDTO userDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .build();
        objectMapper = new ObjectMapper();

        userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPasswordHash("TestPassword123!");
        userDTO.setRole(Role.CLIENT);
        userDTO.setName("Test User");

        userResponseDTO = UserResponseDTO.builder()
                .email("test@example.com")
                .name("Test User")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    @DisplayName("Should register user successfully")
    void testRegisterSuccess() throws Exception {
        when(authService.registerUser(any(UserDTO.class)))
                .thenReturn(userResponseDTO);

        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));

        verify(authService).registerUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Should login successfully")
    void testLoginSuccess() throws Exception {
        Map<String, String> loginData = Map.of(
                "email", "test@example.com",
                "password", "TestPassword123!"
        );

        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(authService).login(eq("test@example.com"), eq("TestPassword123!"), any());
    }

    @Test
    @DisplayName("Should handle login with different credentials")
    void testLoginWithCredentials() throws Exception {
        Map<String, String> loginData = Map.of(
                "email", "another@example.com",
                "password", "AnotherPass123!"
        );

        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginData)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Login successful")));

        verify(authService).login(eq("another@example.com"), eq("AnotherPass123!"), any());
    }

    @Test
    @DisplayName("Should handle invalid user data on register")
    void testRegisterInvalid() throws Exception {
        UserDTO invalidDTO = new UserDTO();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerUser(any());
    }
}
