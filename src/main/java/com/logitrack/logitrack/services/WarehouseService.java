package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.Warehouse.WarehouseDTO;
import com.logitrack.logitrack.dtos.Warehouse.WarehouseRespDTO;
import com.logitrack.logitrack.mapper.WarehouseMapper;
import com.logitrack.logitrack.models.WAREHOUSE_MANAGER;
import com.logitrack.logitrack.models.Warehouse;
import com.logitrack.logitrack.repositories.WarehouseManagerRepository;
import com.logitrack.logitrack.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseManagerRepository warehouseManagerRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseRespDTO addWarehouse(WarehouseDTO warehouseDTO) {

        Warehouse warehouse = warehouseMapper.toEntity(warehouseDTO);
        Optional<WAREHOUSE_MANAGER> warehouseManager= warehouseManagerRepository.findById(warehouseDTO.getWarehouseManagerId());
        if (warehouseManager.isEmpty()) {
            throw new IllegalArgumentException("Warehouse Manager with id " + warehouseDTO.getWarehouseManagerId() + " not found.");
        }
        warehouse.setWarehouse_manager(warehouseManager.get());
        warehouseRepository.save(warehouse);
        return warehouseMapper.toResponseDTO(warehouse);
    }

    public List<WarehouseRespDTO> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        return warehouses.stream()
                .map(warehouseMapper::toResponseDTO)
                .toList();
    }

    public WarehouseRespDTO getWarehouseByCode(String code) {
        Warehouse warehouse = warehouseRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse with code " + code + " not found."));
        return warehouseMapper.toResponseDTO(warehouse);
    }
    public WarehouseRespDTO updateWarehouse(String code, WarehouseDTO warehouseDTO) {
        Warehouse existingWarehouse = warehouseRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse with code " + code + " not found."));
        warehouseMapper.updateWarehouseFromDto(warehouseDTO, existingWarehouse);
        warehouseRepository.save(existingWarehouse);
        return warehouseMapper.toResponseDTO(existingWarehouse);
    }
    public void deleteWarehouseByCode(String code) {
        Warehouse warehouse = warehouseRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse with code " + code + " not found."));
        warehouseRepository.delete(warehouse);
    }
}
