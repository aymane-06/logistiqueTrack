package com.logitrack.logitrack.audit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;

/**
 * Service for logging business-related audit events.
 * Logs order lifecycle, inventory movements, shipments, and other business operations.
 * All logs include business identifiers for traceability.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BusinessAuditService {

    /**
     * Log sales order creation
     */
    public void logSalesOrderCreated(UUID orderId, UUID clientId, UUID warehouseId, int lineCount, String status) {
        Map<String, Object> auditData = buildBaseAuditData("sales_order_created");
        auditData.put("order_id", orderId.toString());
        auditData.put("client_id", clientId.toString());
        auditData.put("warehouse_id", warehouseId.toString());
        auditData.put("line_count", lineCount);
        auditData.put("order_status", status);
        auditData.put("business_entity", "sales_order");
        
        log.info("Sales order created", StructuredArguments.entries(auditData));
    }

    /**
     * Log sales order status change
     */
    public void logSalesOrderStatusChange(UUID orderId, String oldStatus, String newStatus, String reason) {
        Map<String, Object> auditData = buildBaseAuditData("sales_order_status_change");
        auditData.put("order_id", orderId.toString());
        auditData.put("old_status", oldStatus);
        auditData.put("new_status", newStatus);
        auditData.put("reason", reason);
        auditData.put("business_entity", "sales_order");
        
        log.info("Sales order status changed", StructuredArguments.entries(auditData));
    }

    /**
     * Log purchase order creation
     */
    public void logPurchaseOrderCreated(UUID orderId, UUID supplierId, UUID warehouseId, int lineCount) {
        Map<String, Object> auditData = buildBaseAuditData("purchase_order_created");
        auditData.put("order_id", orderId.toString());
        auditData.put("supplier_id", supplierId.toString());
        auditData.put("warehouse_id", warehouseId.toString());
        auditData.put("line_count", lineCount);
        auditData.put("business_entity", "purchase_order");
        
        log.info("Purchase order created", StructuredArguments.entries(auditData));
    }

    /**
     * Log purchase order status change
     */
    public void logPurchaseOrderStatusChange(UUID orderId, String oldStatus, String newStatus) {
        Map<String, Object> auditData = buildBaseAuditData("purchase_order_status_change");
        auditData.put("order_id", orderId.toString());
        auditData.put("old_status", oldStatus);
        auditData.put("new_status", newStatus);
        auditData.put("business_entity", "purchase_order");
        
        log.info("Purchase order status changed", StructuredArguments.entries(auditData));
    }

    /**
     * Log inventory movement (stock in/out)
     */
    public void logInventoryMovement(UUID productId, UUID warehouseId, String movementType, 
                                    int quantity, int newStock, String reason) {
        Map<String, Object> auditData = buildBaseAuditData("inventory_movement");
        auditData.put("product_id", productId.toString());
        auditData.put("warehouse_id", warehouseId.toString());
        auditData.put("movement_type", movementType); // INBOUND, OUTBOUND, ADJUSTMENT
        auditData.put("quantity", quantity);
        auditData.put("new_stock_level", newStock);
        auditData.put("reason", reason);
        auditData.put("business_entity", "inventory");
        
        log.info("Inventory movement recorded", StructuredArguments.entries(auditData));
    }

    /**
     * Log stock reservation
     */
    public void logStockReservation(UUID orderId, UUID productId, UUID warehouseId, int quantity) {
        Map<String, Object> auditData = buildBaseAuditData("stock_reservation");
        auditData.put("order_id", orderId.toString());
        auditData.put("product_id", productId.toString());
        auditData.put("warehouse_id", warehouseId.toString());
        auditData.put("quantity", quantity);
        auditData.put("business_entity", "inventory");
        
        log.info("Stock reserved for order", StructuredArguments.entries(auditData));
    }

    /**
     * Log shipment creation
     */
    public void logShipmentCreated(UUID shipmentId, UUID orderId, UUID carrierId, String trackingNumber) {
        Map<String, Object> auditData = buildBaseAuditData("shipment_created");
        auditData.put("shipment_id", shipmentId.toString());
        auditData.put("order_id", orderId.toString());
        auditData.put("carrier_id", carrierId.toString());
        auditData.put("tracking_number", trackingNumber);
        auditData.put("business_entity", "shipment");
        
        log.info("Shipment created", StructuredArguments.entries(auditData));
    }

    /**
     * Log shipment status change
     */
    public void logShipmentStatusChange(UUID shipmentId, UUID orderId, String oldStatus, String newStatus) {
        Map<String, Object> auditData = buildBaseAuditData("shipment_status_change");
        auditData.put("shipment_id", shipmentId.toString());
        auditData.put("order_id", orderId.toString());
        auditData.put("old_status", oldStatus);
        auditData.put("new_status", newStatus);
        auditData.put("business_entity", "shipment");
        
        log.info("Shipment status changed", StructuredArguments.entries(auditData));
    }

    /**
     * Log product creation or update
     */
    public void logProductChange(UUID productId, String action, String productName) {
        Map<String, Object> auditData = buildBaseAuditData("product_" + action.toLowerCase());
        auditData.put("product_id", productId.toString());
        auditData.put("product_name", productName);
        auditData.put("action", action); // CREATED, UPDATED, DELETED
        auditData.put("business_entity", "product");
        
        log.info("Product " + action.toLowerCase(), StructuredArguments.entries(auditData));
    }

    /**
     * Log warehouse operation
     */
    public void logWarehouseOperation(UUID warehouseId, String operation, String details) {
        Map<String, Object> auditData = buildBaseAuditData("warehouse_operation");
        auditData.put("warehouse_id", warehouseId.toString());
        auditData.put("operation", operation);
        auditData.put("details", details);
        auditData.put("business_entity", "warehouse");
        
        log.info("Warehouse operation", StructuredArguments.entries(auditData));
    }

    /**
     * Log stock level alert (low stock, out of stock)
     */
    public void logStockAlert(UUID productId, UUID warehouseId, String alertType, int currentStock, int threshold) {
        Map<String, Object> auditData = buildBaseAuditData("stock_alert");
        auditData.put("product_id", productId.toString());
        auditData.put("warehouse_id", warehouseId.toString());
        auditData.put("alert_type", alertType); // LOW_STOCK, OUT_OF_STOCK
        auditData.put("current_stock", currentStock);
        auditData.put("threshold", threshold);
        auditData.put("business_entity", "inventory");
        
        log.warn("Stock alert", StructuredArguments.entries(auditData));
    }

    /**
     * Log business error (e.g., insufficient stock, invalid operation)
     */
    public void logBusinessError(String operation, String errorType, String errorMessage, Map<String, String> context) {
        Map<String, Object> auditData = buildBaseAuditData("business_error");
        auditData.put("operation", operation);
        auditData.put("error_type", errorType);
        auditData.put("error_message", errorMessage);
        if (context != null) {
            auditData.putAll(context);
        }
        
        log.error("Business operation error", StructuredArguments.entries(auditData));
    }

    /**
     * Build base audit data with common fields
     */
    private Map<String, Object> buildBaseAuditData(String eventType) {
        Map<String, Object> data = new HashMap<>();
        data.put("event_type", eventType);
        
        // Add current user info if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            data.put("user_email", auth.getName());
            if (auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
                data.put("user_role", auth.getAuthorities().iterator().next().getAuthority());
            }
        }
        
        return data;
    }
}
