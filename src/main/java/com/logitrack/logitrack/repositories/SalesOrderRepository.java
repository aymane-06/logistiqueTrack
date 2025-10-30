package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
}
