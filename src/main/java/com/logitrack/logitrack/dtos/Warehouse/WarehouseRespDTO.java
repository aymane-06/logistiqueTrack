package com.logitrack.logitrack.dtos.Warehouse;

import com.logitrack.logitrack.dtos.Inventory.InventoryDTO;
import com.logitrack.logitrack.dtos.Inventory.WareHouseInventoryRespDTO;
import com.logitrack.logitrack.dtos.WarehouseManagerDTO;
import lombok.Data;

import java.util.List;

@Data
public class WarehouseRespDTO {
    private String id;
    private String code;
    private String name;
    private String location;
    private Boolean active;
    private WarehouseManagerDTO warehouse_manager;
    private List<WareHouseInventoryRespDTO> inventories;
    private String createdAt;
    private String updatedAt;
}
