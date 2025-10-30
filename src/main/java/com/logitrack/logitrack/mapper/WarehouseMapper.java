package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.WarehouseDTO;
import com.logitrack.logitrack.models.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    WarehouseDTO toDTO(Warehouse warehouse);
    Warehouse toEntity(WarehouseDTO warehouseDTO);
    void updateWarehouseFromDto(WarehouseDTO dto, @MappingTarget Warehouse entity);
}
