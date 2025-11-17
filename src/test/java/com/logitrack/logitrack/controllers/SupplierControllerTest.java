package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.SupplierDTO;
import com.logitrack.logitrack.services.SupplierService;
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
@DisplayName("SupplierController Unit Tests")
class SupplierControllerTest {

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private SupplierController supplierController;

    private UUID supplierId;
    private SupplierDTO supplierDTO;

    @BeforeEach
    void setUp() {
        supplierId = UUID.randomUUID();
        supplierDTO = SupplierDTO.builder()
                .id(supplierId.toString())
                .name("Test Supplier")
                .contactInfo("contact@supplier.com")
                .build();
    }

    @Test
    @DisplayName("Should add supplier and return CREATED status")
    void testAddSupplier() {
        when(supplierService.addSupplier(any(SupplierDTO.class)))
                .thenReturn(supplierDTO);

        ResponseEntity<SupplierDTO> result = supplierController.addSupplier(supplierDTO);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(supplierDTO, result.getBody());
        verify(supplierService).addSupplier(any(SupplierDTO.class));
    }

    @Test
    @DisplayName("Should get all suppliers and return OK status")
    void testGetAllSuppliers() {
        when(supplierService.getAllSuppliers())
                .thenReturn(Arrays.asList(supplierDTO));

        ResponseEntity<Iterable<SupplierDTO>> result = supplierController.getAllSuppliers();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(supplierService).getAllSuppliers();
    }

    @Test
    @DisplayName("Should get supplier by ID and return OK status")
    void testGetSupplierById() {
        when(supplierService.getSupplierById(supplierId))
                .thenReturn(supplierDTO);

        ResponseEntity<SupplierDTO> result = supplierController.getSupplierById(supplierId);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(supplierDTO, result.getBody());
        verify(supplierService).getSupplierById(supplierId);
    }

    @Test
    @DisplayName("Should update supplier and return OK status")
    void testUpdateSupplier() {
        SupplierDTO updatedDTO = SupplierDTO.builder()
                .id(supplierId.toString())
                .name("Updated Supplier")
                .contactInfo("updated@supplier.com")
                .build();

        when(supplierService.updateSupplier(eq(supplierId), any(SupplierDTO.class)))
                .thenReturn(updatedDTO);

        ResponseEntity<SupplierDTO> result = supplierController.updateSupplier(supplierId, updatedDTO);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(updatedDTO, result.getBody());
        verify(supplierService).updateSupplier(eq(supplierId), any(SupplierDTO.class));
    }

    @Test
    @DisplayName("Should delete supplier and return OK status")
    void testDeleteSupplier() {
        ResponseEntity<String> result = supplierController.deleteSupplier(supplierId);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().contains("deleted"));
        verify(supplierService).deleteSupplierById(supplierId);
    }

    @Test
    @DisplayName("Should get empty supplier list")
    void testGetAllSuppliersEmpty() {
        when(supplierService.getAllSuppliers())
                .thenReturn(Arrays.asList());

        ResponseEntity<Iterable<SupplierDTO>> result = supplierController.getAllSuppliers();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(supplierService).getAllSuppliers();
    }
}
