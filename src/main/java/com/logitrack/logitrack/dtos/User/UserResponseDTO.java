package com.logitrack.logitrack.dtos.User;


import com.logitrack.logitrack.models.ENUM.Role;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserResponseDTO {
    private String id;
    private String name;
    private String email;
    private Role role;
    private Boolean active;
}
