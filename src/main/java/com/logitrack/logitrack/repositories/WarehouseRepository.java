package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    boolean existsByCode(String code);
}
