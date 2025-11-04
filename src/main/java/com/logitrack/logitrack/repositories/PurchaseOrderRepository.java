package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus;
import com.logitrack.logitrack.models.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
}
