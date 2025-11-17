package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.Warehouse.WarehouseDTO;
import com.logitrack.logitrack.dtos.Warehouse.WarehouseRespDTO;
import com.logitrack.logitrack.mapper.WarehouseMapper;
import com.logitrack.logitrack.models.WAREHOUSE_MANAGER;
import com.logitrack.logitrack.models.Warehouse;
import com.logitrack.logitrack.repositories.WarehouseManagerRepository;
import com.logitrack.logitrack.repositories.WarehouseRepository;
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
@DisplayName("WarehouseService Tests")
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseManagerRepository warehouseManagerRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseService warehouseService;

    private WarehouseDTO warehouseDTO;
    private Warehouse warehouse;
    private WarehouseRespDTO warehouseRespDTO;
    private WAREHOUSE_MANAGER warehouseManager;
    private UUID managerId;
    private String warehouseCode;

    @BeforeEach
    void setUp() {
        managerId = UUID.randomUUID();
        warehouseCode = "WH-001";

        warehouseDTO = WarehouseDTO.builder()
                .name("Test Warehouse")
                .location("Test Location")
                .warehouseManagerId(managerId)
                .active(true)
                .build();

        warehouse = new Warehouse();
        warehouse.setCode(warehouseCode);
        warehouse.setName("Test Warehouse");

        warehouseRespDTO = WarehouseRespDTO.builder()
                .code(warehouseCode)
                .name("Test Warehouse")
                .location("Test Location")
                .active(true)
                .build();

        warehouseManager = new WAREHOUSE_MANAGER();
        warehouseManager.setId(managerId);
    }

    @Test
    @DisplayName("Should add warehouse successfully")
    void testAddWarehouse() {
        // Arrange
        when(warehouseMapper.toEntity(any(WarehouseDTO.class))).thenReturn(warehouse);
        when(warehouseManagerRepository.findById(managerId)).thenReturn(Optional.of(warehouseManager));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toResponseDTO(any(Warehouse.class))).thenReturn(warehouseRespDTO);

        // Act
        WarehouseRespDTO result = warehouseService.addWarehouse(warehouseDTO);

        // Assert
        assertNotNull(result);
        assertEquals(warehouseCode, result.getCode());
        verify(warehouseManagerRepository, times(1)).findById(managerId);
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Should throw exception when manager not found")
    void testAddWarehouseManagerNotFound() {
        // Arrange
        when(warehouseMapper.toEntity(any(WarehouseDTO.class))).thenReturn(warehouse);
        when(warehouseManagerRepository.findById(managerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            warehouseService.addWarehouse(warehouseDTO);
        });
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all warehouses")
    void testGetAllWarehouses() {
        // Arrange
        List<Warehouse> warehouses = List.of(warehouse);
        when(warehouseRepository.findAll()).thenReturn(warehouses);
        when(warehouseMapper.toResponseDTO(any(Warehouse.class))).thenReturn(warehouseRespDTO);

        // Act
        List<WarehouseRespDTO> result = warehouseService.getAllWarehouses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(warehouseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no warehouses exist")
    void testGetAllWarehousesEmpty() {
        // Arrange
        when(warehouseRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<WarehouseRespDTO> result = warehouseService.getAllWarehouses();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get warehouse by code successfully")
    void testGetWarehouseByCode() {
        // Arrange
        when(warehouseRepository.findByCode(warehouseCode)).thenReturn(Optional.of(warehouse));
        when(warehouseMapper.toResponseDTO(any(Warehouse.class))).thenReturn(warehouseRespDTO);

        // Act
        WarehouseRespDTO result = warehouseService.getWarehouseByCode(warehouseCode);

        // Assert
        assertNotNull(result);
        assertEquals(warehouseCode, result.getCode());
        verify(warehouseRepository, times(1)).findByCode(warehouseCode);
    }

    @Test
    @DisplayName("Should throw exception when warehouse not found by code")
    void testGetWarehouseByCodeNotFound() {
        // Arrange
        when(warehouseRepository.findByCode(warehouseCode)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            warehouseService.getWarehouseByCode(warehouseCode);
        });
    }

    @Test
    @DisplayName("Should update warehouse successfully")
    void testUpdateWarehouse() {
        // Arrange
        when(warehouseRepository.findByCode(warehouseCode)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toResponseDTO(any(Warehouse.class))).thenReturn(warehouseRespDTO);

        // Act
        WarehouseRespDTO result = warehouseService.updateWarehouse(warehouseCode, warehouseDTO);

        // Assert
        assertNotNull(result);
        verify(warehouseRepository, times(1)).findByCode(warehouseCode);
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent warehouse")
    void testUpdateWarehouseNotFound() {
        // Arrange
        when(warehouseRepository.findByCode(warehouseCode)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            warehouseService.updateWarehouse(warehouseCode, warehouseDTO);
        });
    }

    @Test
    @DisplayName("Should delete warehouse successfully")
    void testDeleteWarehouseByCode() {
        // Arrange
        when(warehouseRepository.findByCode(warehouseCode)).thenReturn(Optional.of(warehouse));
        doNothing().when(warehouseRepository).delete(any(Warehouse.class));

        // Act
        warehouseService.deleteWarehouseByCode(warehouseCode);

        // Assert
        verify(warehouseRepository, times(1)).findByCode(warehouseCode);
        verify(warehouseRepository, times(1)).delete(any(Warehouse.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent warehouse")
    void testDeleteWarehouseByCodeNotFound() {
        // Arrange
        when(warehouseRepository.findByCode(warehouseCode)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            warehouseService.deleteWarehouseByCode(warehouseCode);
        });
    }
}
