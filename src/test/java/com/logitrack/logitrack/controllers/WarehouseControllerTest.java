package com.logitrack.logitrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.dtos.Warehouse.WarehouseDTO;
import com.logitrack.logitrack.dtos.Warehouse.WarehouseRespDTO;
import com.logitrack.logitrack.services.WarehouseService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WarehouseControllerTest")
class WarehouseControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private WarehouseService warehouseService;

    private WarehouseDTO warehouseDTO;
    private WarehouseRespDTO warehouseRespDTO;
    private String warehouseCode;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new WarehouseController(warehouseService))
                .build();
        objectMapper = new ObjectMapper();

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
    @DisplayName("Should initialize warehouse successfully")
    void testInitializeWarehouses() throws Exception {
        when(warehouseService.addWarehouse(any(WarehouseDTO.class))).thenReturn(warehouseRespDTO);
        
        ResultActions response = mockMvc.perform(post("/api/warehouses/initialize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(warehouseDTO)));
        
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(warehouseCode))
                .andExpect(jsonPath("$.name").value("Main Warehouse"));

        verify(warehouseService).addWarehouse(any(WarehouseDTO.class));
    }

    @Test
    @DisplayName("Should get all warehouses")
    void testGetWarehouses() throws Exception {
        WarehouseRespDTO warehouse2 = WarehouseRespDTO.builder()
                .code("WH-002")
                .name("Secondary Warehouse")
                .location("Los Angeles")
                .active(true)
                .build();

        when(warehouseService.getAllWarehouses()).thenReturn(List.of(warehouseRespDTO, warehouse2));
        
        ResultActions response = mockMvc.perform(get("/api/warehouses/all")
                .contentType(MediaType.APPLICATION_JSON));
        
        response.andDo(print())
                .andExpect(status().isOk());

        verify(warehouseService).getAllWarehouses();
    }

    @Test
    @DisplayName("Should get warehouse by code successfully")
    void testGetWarehouseByCode() throws Exception {
        when(warehouseService.getWarehouseByCode(warehouseCode)).thenReturn(warehouseRespDTO);
        
        ResultActions response = mockMvc.perform(get("/api/warehouses/{code}", warehouseCode)
                .contentType(MediaType.APPLICATION_JSON));
        
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(warehouseCode))
                .andExpect(jsonPath("$.name").value("Main Warehouse"));

        verify(warehouseService).getWarehouseByCode(warehouseCode);
    }

    @Test
    @DisplayName("Should update warehouse successfully")
    void testUpdateWarehouse() throws Exception {
        WarehouseRespDTO updatedDTO = WarehouseRespDTO.builder()
                .code(warehouseCode)
                .name("Updated Warehouse")
                .location("Los Angeles")
                .active(true)
                .build();
        
        when(warehouseService.updateWarehouse(anyString(), any(WarehouseDTO.class)))
                .thenReturn(updatedDTO);
        
        ResultActions response = mockMvc.perform(put("/api/warehouses/update/{code}", warehouseCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(warehouseDTO)));
        
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Los Angeles"));

        verify(warehouseService).updateWarehouse(anyString(), any(WarehouseDTO.class));
    }

        @Test
    @DisplayName("Should delete warehouse successfully")
    void testDeleteWarehouse() throws Exception {
        doNothing().when(warehouseService).deleteWarehouseByCode(warehouseCode);
        
        ResultActions response = mockMvc.perform(delete("/api/warehouses/delete/{code}", warehouseCode)
                .contentType(MediaType.APPLICATION_JSON));
        
        response.andDo(print())
                .andExpect(status().isOk());

        verify(warehouseService).deleteWarehouseByCode(warehouseCode);
    }
}
