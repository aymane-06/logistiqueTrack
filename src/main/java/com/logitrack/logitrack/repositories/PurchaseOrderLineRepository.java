package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine, UUID> {
}
