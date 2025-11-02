package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySku(String sku);
    Optional<Product> findBySku(String sku);
    Optional<Product> findByIdAndActive(UUID id, boolean active);


}
