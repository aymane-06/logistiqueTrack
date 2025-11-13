package com.logitrack.logitrack.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierDTO {
    private String id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @Size(max = 500, message = "Contact info must be at most 500 characters")
    private String contactInfo;

    private String createdAt;

    private String updatedAt;
}
