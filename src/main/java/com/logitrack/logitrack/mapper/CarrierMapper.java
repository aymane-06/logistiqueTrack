package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.CarrierDTO;
import com.logitrack.logitrack.dtos.CarrierRespDTO;
import com.logitrack.logitrack.models.Carrier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarrierMapper {
    CarrierDTO toDTO(Carrier carrier);
    CarrierRespDTO toRespDTO(Carrier carrier);
    Carrier toEntity(CarrierDTO carrierDTO);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateCarrierFromDto(CarrierDTO dto, @MappingTarget Carrier entity);
}
