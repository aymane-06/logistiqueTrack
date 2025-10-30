package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.CarrierDTO;
import com.logitrack.logitrack.models.Carrier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarrierMapper {
    CarrierDTO toDTO(Carrier carrier);
    Carrier toEntity(CarrierDTO carrierDTO);
    void updateCarrierFromDto(CarrierDTO dto, @MappingTarget Carrier entity);
}
