package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.SupplierDTO;
import com.logitrack.logitrack.services.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService supplierService;


    @PostMapping("/add")
    public ResponseEntity<SupplierDTO> addSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.addSupplier(supplierDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<SupplierDTO>> getAllSuppliers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.getSupplierById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable UUID id, @Valid @RequestBody SupplierDTO supplierDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(supplierService.updateSupplier(id, supplierDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable UUID id) {
        supplierService.deleteSupplierById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Supplier with id: " + id + " has been deleted.");
    }
}
