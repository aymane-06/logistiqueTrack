package com.logitrack.logitrack.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HomeControllerTest")
class HomeControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new HomeController())
                .build();
    }

    @Test
    @DisplayName("Should return API running message")
    void testHomeEndpoint() throws Exception {
        ResultActions response = mockMvc.perform(get("/"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Digital Logistics Supply Chain API is running."));
    }

    @Test
    @DisplayName("Should return non-empty string from home endpoint")
    void testHomeEndpointNotEmpty() throws Exception {
        ResultActions response = mockMvc.perform(get("/"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyString())));
    }

    @Test
    @DisplayName("Should return exact message from home endpoint")
    void testHomeEndpointExactMessage() throws Exception {
        String expectedMessage = "Digital Logistics Supply Chain API is running.";

        ResultActions response = mockMvc.perform(get("/"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    @DisplayName("Should return status OK on home request")
    void testHomeStatusOk() throws Exception {
        ResultActions response = mockMvc.perform(get("/"));

        response.andDo(print())
                .andExpect(status().isOk());
    }
}
