package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.SupplierDTO;
import com.logitrack.logitrack.mapper.SupplierMapper;
import com.logitrack.logitrack.models.Supplier;
import com.logitrack.logitrack.repositories.SupplierRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierDTO addSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
         supplierRepository.save(supplier);
            return supplierMapper.toDTO(supplier);
    }

    public List<SupplierDTO> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(supplierMapper::toDTO)
                .toList();
    }

    public SupplierDTO getSupplierById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with id " + id + " not found."));
        return supplierMapper.toDTO(supplier);
    }


    public SupplierDTO updateSupplier(UUID id, @Valid SupplierDTO supplierDTO) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with id " + id + " not found."));
        supplierMapper.updateSupplierFromDto(supplierDTO, existingSupplier);
        supplierRepository.save(existingSupplier);
        return supplierMapper.toDTO(existingSupplier);
    }

    public void deleteSupplierById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with id " + id + " not found."));
        supplierRepository.delete(supplier);
    }
}
