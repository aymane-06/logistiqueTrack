package com.logitrack.logitrack.dtos.Warehouse;

import com.logitrack.logitrack.dtos.Inventory.WareHouseInventoryRespDTO;
import com.logitrack.logitrack.dtos.WarehouseManagerDTO;
import lombok.Data;

import java.util.List;

@Data
public class OrderWarehouseRespDTO {
    private String id;
    private String code;
    private String name;
    private String location;
    private Boolean active;
    private WarehouseManagerDTO warehouse_manager;
    private String createdAt;
    private String updatedAt;
}
