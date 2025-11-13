package com.logitrack.logitrack.dtos.Warehouse;


import java.util.UUID;

import com.logitrack.logitrack.dtos.WarehouseManagerDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderWarehouseRespDTO {
    private UUID id;
    private String code;
    private String name;
    private String location;
    private Boolean active;
    private WarehouseManagerDTO warehouse_manager;
    private String createdAt;
    private String updatedAt;
}
