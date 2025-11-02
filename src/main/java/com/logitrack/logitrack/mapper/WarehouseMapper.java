package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.Warehouse.WarehouseDTO;
import com.logitrack.logitrack.dtos.Warehouse.WarehouseRespDTO;
import com.logitrack.logitrack.models.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring" , uses = {WarehouseManagerMapper.class})
public interface WarehouseMapper {
    WarehouseDTO toDTO(Warehouse warehouse);
    Warehouse toEntity(WarehouseDTO warehouseDTO);
    void updateWarehouseFromDto(WarehouseDTO dto, @MappingTarget Warehouse entity);
    WarehouseRespDTO toResponseDTO(Warehouse warehouse);
}
