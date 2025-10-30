package com.logitrack.logitrack.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientDTO {
    private String id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    private String contactInfo;

    private String createdAt;

    private String updatedAt;
}
