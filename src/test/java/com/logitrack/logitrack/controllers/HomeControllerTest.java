package com.logitrack.logitrack.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("HomeController Unit Tests")
class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;

    @Test
    @DisplayName("Should return API running message")
    void testHomeEndpoint() {
        String result = homeController.home();

        assertNotNull(result);
        assertTrue(result.contains("Digital Logistics Supply Chain API is running"));
    }

    @Test
    @DisplayName("Should return non-empty string")
    void testHomeEndpointNotEmpty() {
        String result = homeController.home();

        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    @Test
    @DisplayName("Should return exact message")
    void testHomeEndpointExactMessage() {
        String expected = "Digital Logistics Supply Chain API is running.";
        String result = homeController.home();

        assertTrue(result.equals(expected));
    }
}
