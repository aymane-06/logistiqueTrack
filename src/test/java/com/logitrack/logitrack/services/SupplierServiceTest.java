package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.SupplierDTO;
import com.logitrack.logitrack.mapper.SupplierMapper;
import com.logitrack.logitrack.models.Supplier;
import com.logitrack.logitrack.repositories.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierService Tests")
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private SupplierDTO supplierDTO;
    private Supplier supplier;
    private UUID supplierId;

    @BeforeEach
    void setUp() {
        supplierId = UUID.randomUUID();

        supplierDTO = SupplierDTO.builder()
                .id(supplierId.toString())
                .name("Test Supplier")
                .contactInfo("contact@supplier.com")
                .build();

        supplier = new Supplier();
        supplier.setId(supplierId);
        supplier.setName("Test Supplier");
        supplier.setContactInfo("contact@supplier.com");
    }

    @Test
    @DisplayName("Should add supplier successfully")
    void testAddSupplier() {
        // Arrange
        when(supplierMapper.toEntity(any(SupplierDTO.class))).thenReturn(supplier);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(supplierMapper.toDTO(any(Supplier.class))).thenReturn(supplierDTO);

        // Act
        SupplierDTO result = supplierService.addSupplier(supplierDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test Supplier", result.getName());
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Should get all suppliers")
    void testGetAllSuppliers() {
        // Arrange
        List<Supplier> suppliers = List.of(supplier);
        when(supplierRepository.findAll()).thenReturn(suppliers);
        when(supplierMapper.toDTO(any(Supplier.class))).thenReturn(supplierDTO);

        // Act
        List<SupplierDTO> result = supplierService.getAllSuppliers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(supplierRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no suppliers exist")
    void testGetAllSuppliersEmpty() {
        // Arrange
        when(supplierRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<SupplierDTO> result = supplierService.getAllSuppliers();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get supplier by id successfully")
    void testGetSupplierById() {
        // Arrange
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toDTO(any(Supplier.class))).thenReturn(supplierDTO);

        // Act
        SupplierDTO result = supplierService.getSupplierById(supplierId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Supplier", result.getName());
        verify(supplierRepository, times(1)).findById(supplierId);
    }

    @Test
    @DisplayName("Should throw exception when supplier not found")
    void testGetSupplierByIdNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(supplierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            supplierService.getSupplierById(nonExistentId);
        });
        verify(supplierRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should update supplier successfully")
    void testUpdateSupplier() {
        // Arrange
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(supplierMapper.toDTO(any(Supplier.class))).thenReturn(supplierDTO);

        // Act
        SupplierDTO result = supplierService.updateSupplier(supplierId, supplierDTO);

        // Assert
        assertNotNull(result);
        verify(supplierRepository, times(1)).findById(supplierId);
        verify(supplierRepository, times(1)).save(any(Supplier.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent supplier")
    void testUpdateSupplierNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(supplierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            supplierService.updateSupplier(nonExistentId, supplierDTO);
        });
    }

    @Test
    @DisplayName("Should delete supplier successfully")
    void testDeleteSupplierById() {
        // Arrange
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        doNothing().when(supplierRepository).delete(any(Supplier.class));

        // Act
        supplierService.deleteSupplierById(supplierId);

        // Assert
        verify(supplierRepository, times(1)).findById(supplierId);
        verify(supplierRepository, times(1)).delete(any(Supplier.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent supplier")
    void testDeleteSupplierByIdNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(supplierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            supplierService.deleteSupplierById(nonExistentId);
        });
    }
}
