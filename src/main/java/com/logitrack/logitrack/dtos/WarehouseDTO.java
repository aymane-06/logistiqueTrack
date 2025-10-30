package com.logitrack.logitrack.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WarehouseDTO {
    private String id;

    @NotBlank(message = "Warehouse code is required")
    @Size(max = 50, message = "Warehouse code must be at most 50 characters")
    private String code;

    @NotBlank(message = "Warehouse name is required")
    @Size(max = 255, message = "Warehouse name must be at most 255 characters")
    private String name;

    private Boolean active;

    private String createdAt;

    private String updatedAt;
}
