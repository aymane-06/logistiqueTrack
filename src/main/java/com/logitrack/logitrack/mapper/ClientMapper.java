package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.ClientDTO;
import com.logitrack.logitrack.models.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDTO toDTO(Client client);
    Client toEntity(ClientDTO clientDTO);
    void updateClientFromDto(ClientDTO dto, @MappingTarget Client entity);
}
