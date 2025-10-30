package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.SalesOrderDTO;
import com.logitrack.logitrack.models.SalesOrder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SalesOrderMapper {
    SalesOrderDTO toDTO(SalesOrder salesOrder);
    SalesOrder toEntity(SalesOrderDTO salesOrderDTO);
    void updateSalesOrderFromDto(SalesOrderDTO dto, @MappingTarget SalesOrder entity);
}
