package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.Shipment.ShipmentDTO;
import com.logitrack.logitrack.dtos.Shipment.ShipmentRespDto;
import com.logitrack.logitrack.models.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {
    ShipmentDTO toDTO(Shipment shipment);
    Shipment toEntity(ShipmentDTO shipmentDTO);
    @Mapping(target = "salesOrderId", source = "salesOrder.id")
    ShipmentRespDto toRespDto(Shipment shipment);
    void updateShipmentFromDto(ShipmentDTO dto, @MappingTarget Shipment entity);
}
