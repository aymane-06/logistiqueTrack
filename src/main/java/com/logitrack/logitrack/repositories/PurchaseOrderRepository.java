package com.logitrack.logitrack.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logitrack.logitrack.models.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
}
