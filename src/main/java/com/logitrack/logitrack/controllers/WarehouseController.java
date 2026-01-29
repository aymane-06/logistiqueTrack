package com.logitrack.logitrack.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.logitrack.logitrack.dtos.Warehouse.WarehouseDTO;
import com.logitrack.logitrack.dtos.Warehouse.WarehouseRespDTO;
import com.logitrack.logitrack.services.WarehouseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PostMapping("")
    public ResponseEntity<WarehouseRespDTO> initializeWarehouses(@RequestBody WarehouseDTO warehouseDTO) {
        WarehouseRespDTO warehouseRespDTO = warehouseService.addWarehouse(warehouseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseRespDTO);
    }

    @GetMapping("")
    public List<WarehouseRespDTO> getWarehouses() {
        return warehouseService.getAllWarehouses();
    }

    @GetMapping("/{code}")
    public ResponseEntity<WarehouseRespDTO> getWarehouseByCode(@PathVariable String code) {
        WarehouseRespDTO warehouseRespDTO = warehouseService.getWarehouseByCode(code);
        return ResponseEntity.ok(warehouseRespDTO);
    }

    @PutMapping("/{code}")
    public ResponseEntity<WarehouseRespDTO> updateWarehouse(@PathVariable String code, @RequestBody WarehouseDTO warehouseDTO) {
        WarehouseRespDTO warehouseRespDTO = warehouseService.updateWarehouse(code, warehouseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(warehouseRespDTO);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<String> deleteWarehouse(@PathVariable String code) {
        warehouseService.deleteWarehouseByCode(code);
        return ResponseEntity.status(HttpStatus.OK).body("Warehouse with code: " + code + " has been deleted.");
    }

}
