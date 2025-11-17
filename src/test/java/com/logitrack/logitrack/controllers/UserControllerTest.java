package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.models.User;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserControllerTest")
class UserControllerTest {

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private String sessionId;
    private Map<String, String> requestBody;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID().toString();
        testUser = User.builder()
                .email("test@example.com")
                .name("Test User")
                .build();
        requestBody = new HashMap<>();
        requestBody.put("sessionId", sessionId);
    }

    @Test
    void testGetCurrentUserSuccess() {
        when(httpSession.getAttribute(sessionId)).thenReturn(testUser);
        
        Object result = userController.getCurrentUser(httpSession, requestBody);
        
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(httpSession).getAttribute(sessionId);
    }

    @Test
    void testGetCurrentUserNotFound() {
        when(httpSession.getAttribute(sessionId)).thenReturn(null);
        
        Object result = userController.getCurrentUser(httpSession, requestBody);
        
        assertNull(result);
        verify(httpSession).getAttribute(sessionId);
    }

    @Test
    void testGetCurrentUserWithDifferentSessionId() {
        String differentSessionId = UUID.randomUUID().toString();
        Map<String, String> newRequestBody = new HashMap<>();
        newRequestBody.put("sessionId", differentSessionId);
        
        when(httpSession.getAttribute(differentSessionId)).thenReturn(testUser);
        
        Object result = userController.getCurrentUser(httpSession, newRequestBody);
        
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(httpSession).getAttribute(differentSessionId);
    }
}
