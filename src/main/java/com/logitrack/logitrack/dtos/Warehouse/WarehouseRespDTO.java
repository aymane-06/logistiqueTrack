package com.logitrack.logitrack.dtos.Warehouse;

import com.logitrack.logitrack.dtos.WarehouseManagerDTO;
import com.logitrack.logitrack.models.WAREHOUSE_MANAGER;
import lombok.Data;

@Data
public class WarehouseRespDTO {
    private String code;
    private String name;
    private String location;
    private Boolean active;
    private WarehouseManagerDTO warehouse_manager;
    private String createdAt;
    private String updatedAt;
}
