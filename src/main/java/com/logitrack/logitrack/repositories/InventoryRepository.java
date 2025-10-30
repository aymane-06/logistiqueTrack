package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.Inventory;
import com.logitrack.logitrack.models.Product;
import com.logitrack.logitrack.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);
}
