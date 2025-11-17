package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.Warehouse.WarehouseDTO;
import com.logitrack.logitrack.dtos.Warehouse.WarehouseRespDTO;
import com.logitrack.logitrack.services.WarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WarehouseControllerTest")
class WarehouseControllerTest {

    @Mock
    private WarehouseService warehouseService;

    @InjectMocks
    private WarehouseController warehouseController;

    private WarehouseDTO warehouseDTO;
    private WarehouseRespDTO warehouseRespDTO;
    private String warehouseCode;

    @BeforeEach
    void setUp() {
        warehouseCode = "WH-001";
        
        warehouseDTO = WarehouseDTO.builder()
                .name("Main Warehouse")
                .location("New York")
                .warehouseManagerId(UUID.randomUUID())
                .active(true)
                .build();
        
        warehouseRespDTO = WarehouseRespDTO.builder()
                .code(warehouseCode)
                .name("Main Warehouse")
                .location("New York")
                .active(true)
                .build();
    }

    @Test
    void testInitializeWarehouses() {
        when(warehouseService.addWarehouse(any(WarehouseDTO.class))).thenReturn(warehouseRespDTO);
        
        ResponseEntity<WarehouseRespDTO> result = warehouseController.initializeWarehouses(warehouseDTO);
        
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(warehouseCode, result.getBody().getCode());
        verify(warehouseService).addWarehouse(any(WarehouseDTO.class));
    }

    @Test
    void testGetWarehouses() {
        List<WarehouseRespDTO> warehouses = new ArrayList<>();
        warehouses.add(warehouseRespDTO);
        when(warehouseService.getAllWarehouses()).thenReturn(warehouses);
        
        List<WarehouseRespDTO> result = warehouseController.getWarehouses();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(warehouseCode, result.get(0).getCode());
        verify(warehouseService).getAllWarehouses();
    }

    @Test
    void testGetWarehousesEmpty() {
        when(warehouseService.getAllWarehouses()).thenReturn(new ArrayList<>());
        
        List<WarehouseRespDTO> result = warehouseController.getWarehouses();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(warehouseService).getAllWarehouses();
    }

    @Test
    void testGetWarehouseByCode() {
        when(warehouseService.getWarehouseByCode(warehouseCode)).thenReturn(warehouseRespDTO);
        
        ResponseEntity<WarehouseRespDTO> result = warehouseController.getWarehouseByCode(warehouseCode);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(warehouseCode, result.getBody().getCode());
        verify(warehouseService).getWarehouseByCode(warehouseCode);
    }

    @Test
    void testUpdateWarehouse() {
        WarehouseRespDTO updatedDTO = WarehouseRespDTO.builder()
                .code(warehouseCode)
                .name("Updated Warehouse")
                .location("Los Angeles")
                .active(true)
                .build();
        
        when(warehouseService.updateWarehouse(anyString(), any(WarehouseDTO.class)))
                .thenReturn(updatedDTO);
        
        ResponseEntity<WarehouseRespDTO> result = warehouseController.updateWarehouse(warehouseCode, warehouseDTO);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Los Angeles", result.getBody().getLocation());
        verify(warehouseService).updateWarehouse(anyString(), any(WarehouseDTO.class));
    }

    @Test
    void testDeleteWarehouse() {
        doNothing().when(warehouseService).deleteWarehouseByCode(warehouseCode);
        
        ResponseEntity<String> result = warehouseController.deleteWarehouse(warehouseCode);
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().contains(warehouseCode));
        verify(warehouseService).deleteWarehouseByCode(warehouseCode);
    }
}
