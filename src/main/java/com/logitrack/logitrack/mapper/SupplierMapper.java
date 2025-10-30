package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.SupplierDTO;
import com.logitrack.logitrack.models.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierDTO toDTO(Supplier supplier);
    Supplier toEntity(SupplierDTO supplierDTO);
    void updateSupplierFromDto(SupplierDTO dto, @MappingTarget Supplier entity);
}
