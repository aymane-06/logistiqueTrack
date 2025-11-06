package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.CarrierDTO;
import com.logitrack.logitrack.dtos.CarrierRespDTO;
import com.logitrack.logitrack.services.CarrierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carriers")
public class CarrierController {
    private final CarrierService carrierService;

    @PostMapping("/add")
    public ResponseEntity<CarrierRespDTO> addCarrier(@Valid @RequestBody CarrierDTO carrierDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carrierService.addCarrier(carrierDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CarrierRespDTO>> getAllCarriers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(carrierService.getAllCarriers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrierRespDTO> getCarrierById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(carrierService.getCarrierById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CarrierRespDTO> updateCarrier(@PathVariable UUID id, @Valid @RequestBody CarrierDTO carrierDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(carrierService.updateCarrier(id, carrierDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCarrier(@PathVariable UUID id) {
        carrierService.deleteCarrierById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Carrier with id: " + id + " has been deleted.");
    }
}
