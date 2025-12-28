# 🔌 Guide d'Intégration des Services d'Audit

Ce guide explique comment intégrer les services d'audit dans vos services métier.

## 📦 Services Disponibles

### 1. SecurityAuditService
Pour les événements de sécurité (déjà intégré dans AuthService)

### 2. BusinessAuditService
Pour les événements métier logistique

---

## 💡 Exemples d'Intégration

### Exemple 1: Dans SalesOrderService

```java
package com.logitrack.logitrack.services;

import com.logitrack.logitrack.audit.BusinessAuditService;
// ... autres imports

@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final BusinessAuditService businessAuditService; // ✅ Ajouter cette dépendance

    public SalesOrderRespDTO createSalesOrder(SalesOrderDTO salesOrderDTO) {
        SalesOrder salesOrder = salesOrderMapper.toEntity(salesOrderDTO);
        salesOrder.setStatus(OrderStatus.CREATED);
        salesOrderRepository.save(salesOrder);
        
        // ✅ LOG: Création de commande
        businessAuditService.logSalesOrderCreated(
            salesOrder.getId(),
            salesOrder.getClient().getId(),
            salesOrder.getWarehouse().getId(),
            salesOrder.getLines().size(),
            salesOrder.getStatus().name()
        );
        
        return salesOrderMapper.toRespDTO(salesOrder);
    }

    public Object reserveSalesOrder(UUID id) {
        SalesOrder salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sales Order not found"));
        
        String oldStatus = salesOrder.getStatus().name();
        salesOrder.setStatus(OrderStatus.RESERVED);
        salesOrderRepository.save(salesOrder);
        
        // ✅ LOG: Changement de statut
        businessAuditService.logSalesOrderStatusChange(
            id, 
            oldStatus, 
            OrderStatus.RESERVED.name(),
            "Stock reserved successfully"
        );
        
        // ✅ LOG: Réservation de stock pour chaque ligne
        for (SalesOrderLine line : salesOrder.getLines()) {
            businessAuditService.logStockReservation(
                id,
                line.getProduct().getId(),
                salesOrder.getWarehouse().getId(),
                line.getQuantity()
            );
        }
        
        return salesOrderMapper.toRespDTO(salesOrder);
    }
}
```

### Exemple 2: Dans InventoryService

```java
package com.logitrack.logitrack.services;

import com.logitrack.logitrack.audit.BusinessAuditService;
// ... autres imports

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BusinessAuditService businessAuditService; // ✅ Ajouter

    public void adjustStock(UUID productId, UUID warehouseId, int quantity, String reason) {
        Inventory inventory = findOrCreateInventory(productId, warehouseId);
        
        int oldStock = inventory.getQuantity();
        inventory.setQuantity(oldStock + quantity);
        inventoryRepository.save(inventory);
        
        // ✅ LOG: Mouvement de stock
        businessAuditService.logInventoryMovement(
            productId,
            warehouseId,
            quantity > 0 ? "INBOUND" : "OUTBOUND",
            Math.abs(quantity),
            inventory.getQuantity(),
            reason
        );
        
        // ✅ LOG: Alerte si stock bas
        if (inventory.getQuantity() < inventory.getReorderPoint()) {
            businessAuditService.logStockAlert(
                productId,
                warehouseId,
                "LOW_STOCK",
                inventory.getQuantity(),
                inventory.getReorderPoint()
            );
        }
    }
}
```

### Exemple 3: Dans ShipmentService

```java
package com.logitrack.logitrack.services;

import com.logitrack.logitrack.audit.BusinessAuditService;
// ... autres imports

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final BusinessAuditService businessAuditService; // ✅ Ajouter

    public Shipment createShipment(UUID orderId, UUID carrierId, String trackingNumber) {
        Shipment shipment = Shipment.builder()
            .salesOrder(salesOrderRepository.findById(orderId).orElseThrow())
            .carrier(carrierRepository.findById(carrierId).orElseThrow())
            .trackingNumber(trackingNumber)
            .status(ShipmentStatus.PENDING)
            .build();
        
        shipmentRepository.save(shipment);
        
        // ✅ LOG: Création d'expédition
        businessAuditService.logShipmentCreated(
            shipment.getId(),
            orderId,
            carrierId,
            trackingNumber
        );
        
        return shipment;
    }
    
    public void updateShipmentStatus(UUID shipmentId, ShipmentStatus newStatus) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));
        
        String oldStatus = shipment.getStatus().name();
        shipment.setStatus(newStatus);
        shipmentRepository.save(shipment);
        
        // ✅ LOG: Changement de statut d'expédition
        businessAuditService.logShipmentStatusChange(
            shipmentId,
            shipment.getSalesOrder().getId(),
            oldStatus,
            newStatus.name()
        );
    }
}
```

### Exemple 4: Dans ProductService

```java
package com.logitrack.logitrack.services;

import com.logitrack.logitrack.audit.BusinessAuditService;
// ... autres imports

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BusinessAuditService businessAuditService; // ✅ Ajouter

    public Product createProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        productRepository.save(product);
        
        // ✅ LOG: Création de produit
        businessAuditService.logProductChange(
            product.getId(),
            "CREATED",
            product.getName()
        );
        
        return product;
    }
    
    public Product updateProduct(UUID id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        // Update fields...
        productRepository.save(product);
        
        // ✅ LOG: Mise à jour de produit
        businessAuditService.logProductChange(
            id,
            "UPDATED",
            product.getName()
        );
        
        return product;
    }
}
```

---

## 🎯 Points d'Intégration Recommandés

### SecurityAuditService (✅ Déjà fait)
- ✅ Login/Logout (AuthService)
- ✅ Enregistrement utilisateur (AuthService)
- ✅ Refresh token (AuthService)
- ⚠️ 401/403 errors (GlobalExceptionHandler - optionnel)

### BusinessAuditService (À faire)
- 📋 **SalesOrderService**:
  - `logSalesOrderCreated()` dans `createSalesOrder()`
  - `logSalesOrderStatusChange()` dans `reserveSalesOrder()`, `shipSalesOrder()`, `deliverSalesOrder()`
  - `logStockReservation()` lors de la réservation

- 📦 **PurchaseOrderService**:
  - `logPurchaseOrderCreated()` dans `createPurchaseOrder()`
  - `logPurchaseOrderStatusChange()` dans `updateStatus()`

- 🏭 **InventoryService**:
  - `logInventoryMovement()` pour tous les mouvements de stock
  - `logStockAlert()` quand stock < seuil

- 🚚 **ShipmentService** (si existe):
  - `logShipmentCreated()` lors de création
  - `logShipmentStatusChange()` lors des changements de statut

- 📦 **ProductService**:
  - `logProductChange()` pour CRUD operations

---

## ⚠️ Bonnes Pratiques

### DO ✅
- Toujours logger APRÈS le succès de l'opération en base
- Inclure tous les identifiants métier (UUID)
- Utiliser des messages clairs et descriptifs
- Logger dans un try-catch si l'opération peut échouer

### DON'T ❌
- Ne JAMAIS logger de mots de passe, tokens, secrets
- Ne pas logger de données personnelles sensibles (cartes bancaires, etc.)
- Ne pas logger avant la sauvegarde (risque de log sans commit)
- Ne pas ignorer les exceptions d'audit (elles ne doivent pas bloquer le métier)

### Exemple avec gestion d'erreur

```java
public SalesOrderRespDTO createSalesOrder(SalesOrderDTO salesOrderDTO) {
    try {
        SalesOrder salesOrder = salesOrderMapper.toEntity(salesOrderDTO);
        salesOrder.setStatus(OrderStatus.CREATED);
        salesOrderRepository.save(salesOrder);
        
        // ✅ Audit après succès
        businessAuditService.logSalesOrderCreated(
            salesOrder.getId(),
            salesOrder.getClient().getId(),
            salesOrder.getWarehouse().getId(),
            salesOrder.getLines().size(),
            salesOrder.getStatus().name()
        );
        
        return salesOrderMapper.toRespDTO(salesOrder);
    } catch (Exception e) {
        // ✅ Logger l'erreur métier
        businessAuditService.logBusinessError(
            "CREATE_SALES_ORDER",
            "ORDER_CREATION_FAILED",
            e.getMessage(),
            Map.of("client_id", salesOrderDTO.getClientId())
        );
        throw e;
    }
}
```

---

## 🔍 Vérification

Après intégration, vérifiez dans Kibana:

1. **Logs de sécurité**:
   ```
   event_type: "authentication_success"
   event_type: "user_registration"
   ```

2. **Logs métier**:
   ```
   event_type: "sales_order_created"
   event_type: "inventory_movement"
   event_type: "shipment_created"
   ```

---

**📝 Note**: L'intégration dans AuthService est complète. Pour les autres services, suivez les exemples ci-dessus.
