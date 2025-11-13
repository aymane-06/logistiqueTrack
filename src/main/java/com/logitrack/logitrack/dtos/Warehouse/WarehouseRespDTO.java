package com.logitrack.logitrack.dtos.Warehouse;


import com.logitrack.logitrack.dtos.Inventory.WareHouseInventoryRespDTO;
import com.logitrack.logitrack.dtos.WarehouseManagerDTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class WarehouseRespDTO {
    private UUID id;
    private String code;
    private String name;
    private String location;
    private Boolean active;
    private WarehouseManagerDTO warehouse_manager;
    private List<WareHouseInventoryRespDTO> inventories;
    private String createdAt;
    private String updatedAt;
}
