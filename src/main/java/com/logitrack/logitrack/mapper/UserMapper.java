package com.logitrack.logitrack.mapper;

import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.models.Admin;
import com.logitrack.logitrack.models.Client;
import com.logitrack.logitrack.models.User;
import com.logitrack.logitrack.models.WAREHOUSE_MANAGER;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);
    Admin toAdminEntity(UserDTO userDTO);
    Client toClientEntity(UserDTO userDTO);
    WAREHOUSE_MANAGER toWarehouseManagerEntity(UserDTO userDTO);
    void updateUserFromDto(UserDTO dto, @MappingTarget User entity);
    UserResponseDTO toResponseDTO(User user);
}
