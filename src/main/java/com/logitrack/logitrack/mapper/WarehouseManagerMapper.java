package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.WarehouseManagerDTO;
import com.logitrack.logitrack.models.WAREHOUSE_MANAGER;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseManagerMapper {
    WarehouseManagerDTO toDTO(WAREHOUSE_MANAGER manager);
    WAREHOUSE_MANAGER toEntity(WarehouseManagerDTO managerDTO);
}
