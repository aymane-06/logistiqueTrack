package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.CarrierDTO;
import com.logitrack.logitrack.dtos.CarrierRespDTO;
import com.logitrack.logitrack.mapper.CarrierMapper;
import com.logitrack.logitrack.models.Carrier;
import com.logitrack.logitrack.repositories.CarrierRepository;
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
@DisplayName("CarrierService Tests")
class CarrierServiceTest {

    @Mock
    private CarrierRepository carrierRepository;

    @Mock
    private CarrierMapper carrierMapper;

    @InjectMocks
    private CarrierService carrierService;

    private CarrierDTO carrierDTO;
    private Carrier carrier;
    private CarrierRespDTO carrierRespDTO;
    private UUID carrierId;

    @BeforeEach
    void setUp() {
        carrierId = UUID.randomUUID();

        carrierDTO = CarrierDTO.builder()
                .id(carrierId)
                .name("Test Carrier")
                .contactEmail("contact@carrier.com")
                .contactPhone("1234567890")
                .build();

        carrier = new Carrier();
        carrier.setId(carrierId);
        carrier.setName("Test Carrier");

        carrierRespDTO = new CarrierRespDTO();
        carrierRespDTO.setId(carrierId);
        carrierRespDTO.setName("Test Carrier");
    }

    @Test
    @DisplayName("Should add carrier successfully")
    void testAddCarrier() {
        // Arrange
        when(carrierMapper.toEntity(any(CarrierDTO.class))).thenReturn(carrier);
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);
        when(carrierMapper.toRespDTO(any(Carrier.class))).thenReturn(carrierRespDTO);

        // Act
        CarrierRespDTO result = carrierService.addCarrier(carrierDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test Carrier", result.getName());
        verify(carrierRepository, times(1)).save(any(Carrier.class));
    }

    @Test
    @DisplayName("Should get all carriers")
    void testGetAllCarriers() {
        // Arrange
        List<Carrier> carriers = List.of(carrier);
        when(carrierRepository.findAll()).thenReturn(carriers);
        when(carrierMapper.toRespDTO(any(Carrier.class))).thenReturn(carrierRespDTO);

        // Act
        List<CarrierRespDTO> result = carrierService.getAllCarriers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(carrierRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no carriers exist")
    void testGetAllCarriersEmpty() {
        // Arrange
        when(carrierRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<CarrierRespDTO> result = carrierService.getAllCarriers();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get carrier by id successfully")
    void testGetCarrierById() {
        // Arrange
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierMapper.toRespDTO(any(Carrier.class))).thenReturn(carrierRespDTO);

        // Act
        CarrierRespDTO result = carrierService.getCarrierById(carrierId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Carrier", result.getName());
        verify(carrierRepository, times(1)).findById(carrierId);
    }

    @Test
    @DisplayName("Should throw exception when carrier not found")
    void testGetCarrierByIdNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(carrierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            carrierService.getCarrierById(nonExistentId);
        });
    }

    @Test
    @DisplayName("Should update carrier successfully")
    void testUpdateCarrier() {
        // Arrange
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        when(carrierRepository.save(any(Carrier.class))).thenReturn(carrier);
        when(carrierMapper.toRespDTO(any(Carrier.class))).thenReturn(carrierRespDTO);

        // Act
        CarrierRespDTO result = carrierService.updateCarrier(carrierId, carrierDTO);

        // Assert
        assertNotNull(result);
        verify(carrierRepository, times(1)).findById(carrierId);
        verify(carrierRepository, times(1)).save(any(Carrier.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent carrier")
    void testUpdateCarrierNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(carrierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            carrierService.updateCarrier(nonExistentId, carrierDTO);
        });
    }

    @Test
    @DisplayName("Should delete carrier successfully")
    void testDeleteCarrierById() {
        // Arrange
        when(carrierRepository.findById(carrierId)).thenReturn(Optional.of(carrier));
        doNothing().when(carrierRepository).delete(any(Carrier.class));

        // Act
        carrierService.deleteCarrierById(carrierId);

        // Assert
        verify(carrierRepository, times(1)).findById(carrierId);
        verify(carrierRepository, times(1)).delete(any(Carrier.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent carrier")
    void testDeleteCarrierByIdNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(carrierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            carrierService.deleteCarrierById(nonExistentId);
        });
    }
}
