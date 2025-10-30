package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.WAREHOUSE_MANAGER;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WarehouseManagerRepository extends JpaRepository<WAREHOUSE_MANAGER, UUID> {
}
