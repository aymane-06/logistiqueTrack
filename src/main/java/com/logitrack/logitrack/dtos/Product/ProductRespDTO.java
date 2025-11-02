package com.logitrack.logitrack.dtos.Product;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProductRespDTO {
    private UUID id;
    private String sku;
    private String name;
    private String category;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
