package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.Inventory.InventoryDTO;
import com.logitrack.logitrack.models.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    InventoryDTO toDTO(Inventory inventory);
    Inventory toEntity(InventoryDTO inventoryDTO);
    void updateInventoryFromDto(InventoryDTO dto, @MappingTarget Inventory entity);
}
