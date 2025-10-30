package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySku(String sku);
    Product findBySku(String sku);
}
