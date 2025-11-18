package com.logitrack.logitrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.dtos.CarrierDTO;
import com.logitrack.logitrack.dtos.CarrierRespDTO;
import com.logitrack.logitrack.models.ENUM.CarrierStatus;
import com.logitrack.logitrack.services.CarrierService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarrierControllerTest")
class CarrierControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CarrierService carrierService;

    private CarrierDTO carrierDTO;
    private CarrierRespDTO carrierRespDTO;
    private UUID carrierId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CarrierController(carrierService))
                .build();
        objectMapper = new ObjectMapper();

        carrierId = UUID.randomUUID();
        carrierDTO = CarrierDTO.builder()
                .name("Test Carrier")
                .contactEmail("contact@carrier.com")
                .contactPhone("1234567890")
                .build();

        carrierRespDTO = CarrierRespDTO.builder()
                .id(carrierId)
                .name("Test Carrier")
                .contactEmail("contact@carrier.com")
                .contactPhone("1234567890")
                .baseShippingRate(BigDecimal.valueOf(100.00))
                .maxDailyCapacity(50)
                .currentDailyShipments(10)
                .cutOffTime(LocalTime.of(14, 0))
                .status(CarrierStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should add carrier successfully")
    void testAddCarrier() throws Exception {
        when(carrierService.addCarrier(any(CarrierDTO.class))).thenReturn(carrierRespDTO);

        ResultActions response = mockMvc.perform(post("/api/carriers/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carrierDTO)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Carrier"))
                .andExpect(jsonPath("$.contactEmail").value("contact@carrier.com"));

        verify(carrierService).addCarrier(any(CarrierDTO.class));
    }

    @Test
    @DisplayName("Should retrieve all carriers")
    void testGetAllCarriers() throws Exception {
        CarrierRespDTO carrier2 = CarrierRespDTO.builder()
                .id(UUID.randomUUID())
                .name("Carrier 2")
                .contactEmail("contact2@carrier.com")
                .status(CarrierStatus.ACTIVE)
                .build();

        when(carrierService.getAllCarriers()).thenReturn(List.of(carrierRespDTO, carrier2));

        ResultActions response = mockMvc.perform(get("/api/carriers/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(carrierService).getAllCarriers();
    }

    @Test
    @DisplayName("Should get carrier by ID successfully")
    void testGetCarrierById() throws Exception {
        when(carrierService.getCarrierById(carrierId)).thenReturn(carrierRespDTO);

        ResultActions response = mockMvc.perform(get("/api/carriers/{id}", carrierId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Carrier"));

        verify(carrierService).getCarrierById(carrierId);
    }

    @Test
    @DisplayName("Should update carrier successfully")
    void testUpdateCarrier() throws Exception {
        CarrierRespDTO updatedCarrier = CarrierRespDTO.builder()
                .id(carrierId)
                .name("Updated Carrier")
                .contactEmail("updated@carrier.com")
                .status(CarrierStatus.ACTIVE)
                .build();

        when(carrierService.updateCarrier(eq(carrierId), any(CarrierDTO.class)))
                .thenReturn(updatedCarrier);

        ResultActions response = mockMvc.perform(put("/api/carriers/update/{id}", carrierId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carrierDTO)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Carrier"));

        verify(carrierService).updateCarrier(eq(carrierId), any(CarrierDTO.class));
    }

    @Test
    @DisplayName("Should delete carrier successfully")
    void testDeleteCarrier() throws Exception {
        doNothing().when(carrierService).deleteCarrierById(carrierId);

        ResultActions response = mockMvc.perform(delete("/api/carriers/delete/{id}", carrierId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(carrierService).deleteCarrierById(carrierId);
    }

    @Test
    @DisplayName("Should handle invalid carrier data on add")
    void testAddCarrierInvalid() throws Exception {
        CarrierDTO invalidDTO = CarrierDTO.builder()
                .name("")
                .contactEmail("invalid-email")
                .build();

        mockMvc.perform(post("/api/carriers/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(carrierService, never()).addCarrier(any());
    }
}
