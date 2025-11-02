package com.logitrack.logitrack.dtos.Warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class WarehouseDTO {

    @NotBlank(message = "Warehouse name is required")
    @Size(max = 255, message = "Warehouse name must be at most 255 characters")
    private String name;

    @NotBlank(message = "Warehouse location is required")
    @Size(max = 255, message = "Warehouse location must be at most 255 characters")
    private String location;

    @NotNull(message = "Warehouse manager ID is required")
    private UUID warehouseManagerId;

    private Boolean active;

}
