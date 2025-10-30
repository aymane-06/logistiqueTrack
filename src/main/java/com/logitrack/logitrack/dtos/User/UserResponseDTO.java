package com.logitrack.logitrack.dtos.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.mapstruct.Mapper;

@Data
public class UserResponseDTO {
    private String id;
    private String name;
    private String email;
    private String role;
}
