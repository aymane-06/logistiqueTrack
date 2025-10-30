package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.InventoryMovementDTO;
import com.logitrack.logitrack.models.InventoryMovement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryMovementMapper {
    InventoryMovementDTO toDTO(InventoryMovement inventoryMovement);
    InventoryMovement toEntity(InventoryMovementDTO inventoryMovementDTO);
    void updateInventoryMovementFromDto(InventoryMovementDTO dto, @MappingTarget InventoryMovement entity);
}
