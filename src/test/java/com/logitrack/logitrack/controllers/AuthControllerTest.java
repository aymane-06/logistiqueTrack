package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.models.ENUM.Role;
import com.logitrack.logitrack.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private AuthController authController;

    private UserDTO userDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
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
    void testRegisterSuccess() {
        when(authService.registerUser(any(UserDTO.class)))
                .thenReturn(userResponseDTO);

        ResponseEntity<Map<String, Object>> result = authController.register(userDTO);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody().containsKey("message"));
        assertTrue(result.getBody().containsKey("user"));
        verify(authService).registerUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Should login successfully")
    void testLoginSuccess() {
        when(httpSession.getId()).thenReturn("session-123");
        when(authService.login(anyString(), anyString(), any(HttpSession.class)))
                .thenReturn(httpSession);

        Map<String, String> loginData = Map.of(
                "email", "test@example.com",
                "password", "TestPassword123!"
        );

        String result = authController.login(loginData, httpSession);

        assertNotNull(result);
        assertTrue(result.contains("Login successful"));
        verify(authService).login(anyString(), anyString(), any(HttpSession.class));
    }

    @Test
    @DisplayName("Should handle login with different credentials")
    void testLoginWithCredentials() {
        when(httpSession.getId()).thenReturn("session-456");

        Map<String, String> loginData = Map.of(
                "email", "another@example.com",
                "password", "AnotherPass123!"
        );

        String result = authController.login(loginData, httpSession);

        assertNotNull(result);
        assertTrue(result.contains("Session ID"));
        verify(authService).login("another@example.com", "AnotherPass123!", httpSession);
    }
}
