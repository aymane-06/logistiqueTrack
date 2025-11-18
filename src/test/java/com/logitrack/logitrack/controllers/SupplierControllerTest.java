package com.logitrack.logitrack.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.logitrack.dtos.SupplierDTO;
import com.logitrack.logitrack.services.SupplierService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierControllerTest")
class SupplierControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SupplierService supplierService;

    private SupplierDTO supplierDTO;
    private SupplierDTO supplierRespDTO;
    private UUID supplierId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SupplierController(supplierService))
                .build();
        objectMapper = new ObjectMapper();

        supplierId = UUID.randomUUID();
        supplierDTO = SupplierDTO.builder()
                .name("Test Supplier")
                .contactInfo("contact@supplier.com")
                .build();

        supplierRespDTO = SupplierDTO.builder()
                .id(supplierId.toString())
                .name("Test Supplier")
                .contactInfo("contact@supplier.com")
                .build();
    }

    @Test
    @DisplayName("Should add supplier successfully")
    void testAddSupplier() throws Exception {
        when(supplierService.addSupplier(any(SupplierDTO.class))).thenReturn(supplierRespDTO);

        ResultActions response = mockMvc.perform(post("/api/suppliers/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supplierDTO)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Supplier"))
                .andExpect(jsonPath("$.contactInfo").value("contact@supplier.com"));

        verify(supplierService).addSupplier(any(SupplierDTO.class));
    }

    @Test
    @DisplayName("Should retrieve all suppliers")
    void testGetAllSuppliers() throws Exception {
        SupplierDTO supplier2 = SupplierDTO.builder()
                .id(UUID.randomUUID().toString())
                .name("Supplier 2")
                .contactInfo("contact2@supplier.com")
                .build();

        when(supplierService.getAllSuppliers()).thenReturn(List.of(supplierRespDTO, supplier2));

        ResultActions response = mockMvc.perform(get("/api/suppliers/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(supplierService).getAllSuppliers();
    }

    @Test
    @DisplayName("Should get supplier by ID successfully")
    void testGetSupplierById() throws Exception {
        when(supplierService.getSupplierById(supplierId)).thenReturn(supplierRespDTO);

        ResultActions response = mockMvc.perform(get("/api/suppliers/{id}", supplierId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Supplier"));

        verify(supplierService).getSupplierById(supplierId);
    }

    @Test
    @DisplayName("Should update supplier successfully")
    void testUpdateSupplier() throws Exception {
        SupplierDTO updatedSupplier = SupplierDTO.builder()
                .id(supplierId.toString())
                .name("Updated Supplier")
                .contactInfo("updated@supplier.com")
                .build();

        when(supplierService.updateSupplier(eq(supplierId), any(SupplierDTO.class)))
                .thenReturn(updatedSupplier);

        ResultActions response = mockMvc.perform(put("/api/suppliers/update/{id}", supplierId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supplierDTO)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Supplier"));

        verify(supplierService).updateSupplier(eq(supplierId), any(SupplierDTO.class));
    }

    @Test
    @DisplayName("Should delete supplier successfully")
    void testDeleteSupplier() throws Exception {
        doNothing().when(supplierService).deleteSupplierById(supplierId);

        ResultActions response = mockMvc.perform(delete("/api/suppliers/delete/{id}", supplierId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk());

        verify(supplierService).deleteSupplierById(supplierId);
    }

    @Test
    @DisplayName("Should handle invalid supplier data on add")
    void testAddSupplierInvalid() throws Exception {
        SupplierDTO invalidDTO = SupplierDTO.builder()
                .name("")
                .contactInfo("contact@supplier.com")
                .build();

        mockMvc.perform(post("/api/suppliers/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(supplierService, never()).addSupplier(any());
    }
}
