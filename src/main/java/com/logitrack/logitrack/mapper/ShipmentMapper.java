package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.ShipmentDTO;
import com.logitrack.logitrack.models.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {
    ShipmentDTO toDTO(Shipment shipment);
    Shipment toEntity(ShipmentDTO shipmentDTO);
    void updateShipmentFromDto(ShipmentDTO dto, @MappingTarget Shipment entity);
}
