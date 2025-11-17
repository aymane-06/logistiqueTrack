package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.CarrierDTO;
import com.logitrack.logitrack.dtos.CarrierRespDTO;
import com.logitrack.logitrack.services.CarrierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarrierController Unit Tests")
class CarrierControllerTest {

    @Mock
    private CarrierService carrierService;

    @InjectMocks
    private CarrierController carrierController;

    private UUID carrierId;
    private CarrierDTO carrierDTO;
    private CarrierRespDTO carrierRespDTO;

    @BeforeEach
    void setUp() {
        carrierId = UUID.randomUUID();
        carrierDTO = CarrierDTO.builder()
                .name("Test Carrier")
                .contactEmail("carrier@test.com")
                .contactPhone("123-456-7890")
                .build();

        carrierRespDTO = CarrierRespDTO.builder()
                .id(carrierId)
                .name("Test Carrier")
                .contactEmail("carrier@test.com")
                .contactPhone("123-456-7890")
                .build();
    }

    @Test
    @DisplayName("Should add carrier and return CREATED status")
    void testAddCarrier() {
        when(carrierService.addCarrier(any(CarrierDTO.class)))
                .thenReturn(carrierRespDTO);

        ResponseEntity<CarrierRespDTO> result = carrierController.addCarrier(carrierDTO);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(carrierRespDTO, result.getBody());
        verify(carrierService).addCarrier(any(CarrierDTO.class));
    }

    @Test
    @DisplayName("Should get all carriers and return OK status")
    void testGetAllCarriers() {
        when(carrierService.getAllCarriers())
                .thenReturn(Arrays.asList(carrierRespDTO));

        ResponseEntity<java.util.List<CarrierRespDTO>> result = carrierController.getAllCarriers();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(carrierService).getAllCarriers();
    }

    @Test
    @DisplayName("Should get carrier by ID and return OK status")
    void testGetCarrierById() {
        when(carrierService.getCarrierById(carrierId))
                .thenReturn(carrierRespDTO);

        ResponseEntity<CarrierRespDTO> result = carrierController.getCarrierById(carrierId);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(carrierRespDTO, result.getBody());
        verify(carrierService).getCarrierById(carrierId);
    }

    @Test
    @DisplayName("Should update carrier and return OK status")
    void testUpdateCarrier() {
        CarrierRespDTO updatedDTO = CarrierRespDTO.builder()
                .id(carrierId)
                .name("Updated Carrier")
                .contactEmail("updated@carrier.com")
                .contactPhone("999-999-9999")
                .build();

        when(carrierService.updateCarrier(eq(carrierId), any(CarrierDTO.class)))
                .thenReturn(updatedDTO);

        ResponseEntity<CarrierRespDTO> result = carrierController.updateCarrier(carrierId, carrierDTO);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(updatedDTO, result.getBody());
        verify(carrierService).updateCarrier(eq(carrierId), any(CarrierDTO.class));
    }

    @Test
    @DisplayName("Should delete carrier and return OK status")
    void testDeleteCarrier() {
        ResponseEntity<String> result = carrierController.deleteCarrier(carrierId);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().contains("deleted"));
        verify(carrierService).deleteCarrierById(carrierId);
    }

    @Test
    @DisplayName("Should get empty carrier list")
    void testGetAllCarriersEmpty() {
        when(carrierService.getAllCarriers())
                .thenReturn(Arrays.asList());

        ResponseEntity<java.util.List<CarrierRespDTO>> result = carrierController.getAllCarriers();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(carrierService).getAllCarriers();
    }
}
