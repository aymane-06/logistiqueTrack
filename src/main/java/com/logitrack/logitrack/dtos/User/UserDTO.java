package com.logitrack.logitrack.dtos.User;

import com.logitrack.logitrack.models.ENUM.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {
    private String id;

    @NotBlank(message = "Name is required")
    @Size(min = 4, max = 50 , message = "Name must be between 4 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Role is required")
    private Role role;
    @NotBlank(message = "Password is required")
    //@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
          //   message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String passwordHash;

    private String firstName;

    private String lastName;




    private Boolean active = true;

    private String createdAt;

    private String updatedAt;



}
