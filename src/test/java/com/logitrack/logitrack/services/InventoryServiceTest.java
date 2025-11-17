package com.logitrack.logitrack.services;

import com.logitrack.logitrack.models.Inventory;
import com.logitrack.logitrack.models.Product;
import com.logitrack.logitrack.models.Warehouse;
import com.logitrack.logitrack.repositories.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService Tests")
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Warehouse warehouse;
    private Product product;
    private UUID productId;
    private UUID warehouseId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        warehouseId = UUID.randomUUID();

        product = new Product();
        product.setId(productId);
        product.setName("Test Product");

        warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        warehouse.setInventories(new ArrayList<>());
    }

    @Test
    @DisplayName("Should return full quantity when no inventory items found for product")
    void testOutBoundInventoryNoItemsFound() {
        // Arrange
        Integer requestedQuantity = 100;
        warehouse.setInventories(new ArrayList<>());

        // Act
        Integer result = inventoryService.OutBoundInventory(productId, warehouse, requestedQuantity);

        // Assert
        assertEquals(requestedQuantity, result);
    }

    @Test
    @DisplayName("Should return zero when single inventory item has sufficient quantity")
    void testOutBoundInventorySingleItemSufficientQuantity() {
        // Arrange
        Integer requestedQuantity = 50;
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQtyOnHand(100);
        warehouse.getInventories().add(inventory);

        // Act
        Integer result = inventoryService.OutBoundInventory(productId, warehouse, requestedQuantity);

        // Assert
        assertEquals(0, result);
        assertEquals(50, inventory.getQtyOnHand()); // 100 - 50 = 50
    }

    @Test
    @DisplayName("Should return remaining quantity when single item has insufficient quantity")
    void testOutBoundInventorySingleItemInsufficientQuantity() {
        // Arrange
        Integer requestedQuantity = 150;
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQtyOnHand(100);
        warehouse.getInventories().add(inventory);

        // Act
        Integer result = inventoryService.OutBoundInventory(productId, warehouse, requestedQuantity);

        // Assert
        assertEquals(50, result); // 150 - 100 = 50 remaining
        assertEquals(0, inventory.getQtyOnHand()); // Inventory depleted to 0
    }

    @Test
    @DisplayName("Should distribute quantity across multiple inventory items")
    void testOutBoundInventoryMultipleItemsDistribution() {
        // Arrange
        Integer requestedQuantity = 150;

        Inventory inv1 = new Inventory();
        inv1.setProduct(product);
        inv1.setQtyOnHand(100);

        Inventory inv2 = new Inventory();
        inv2.setProduct(product);
        inv2.setQtyOnHand(100);

        warehouse.getInventories().add(inv1);
        warehouse.getInventories().add(inv2);

        // Act
        Integer result = inventoryService.OutBoundInventory(productId, warehouse, requestedQuantity);

        // Assert
        assertEquals(0, result); // 150 fully distributed: 100 + 50 = 150
        assertEquals(0, inv1.getQtyOnHand()); // First item depleted
        assertEquals(50, inv2.getQtyOnHand()); // Second item reduced: 100 - 50 = 50
    }

    @Test
    @DisplayName("Should fully distribute quantity across multiple items with exact depletion")
    void testOutBoundInventoryMultipleItemsExactDepletion() {
        // Arrange
        Integer requestedQuantity = 200;

        Inventory inv1 = new Inventory();
        inv1.setProduct(product);
        inv1.setQtyOnHand(100);

        Inventory inv2 = new Inventory();
        inv2.setProduct(product);
        inv2.setQtyOnHand(100);

        warehouse.getInventories().add(inv1);
        warehouse.getInventories().add(inv2);

        // Act
        Integer result = inventoryService.OutBoundInventory(productId, warehouse, requestedQuantity);

        // Assert
        assertEquals(0, result); // All 200 distributed
        assertEquals(0, inv1.getQtyOnHand()); // Both items depleted
        assertEquals(0, inv2.getQtyOnHand());
    }

    @Test
    @DisplayName("Should return remaining quantity when all items are depleted")
    void testOutBoundInventoryAllItemsDepleted() {
        // Arrange
        Integer requestedQuantity = 300;

        Inventory inv1 = new Inventory();
        inv1.setProduct(product);
        inv1.setQtyOnHand(100);

        Inventory inv2 = new Inventory();
        inv2.setProduct(product);
        inv2.setQtyOnHand(100);

        warehouse.getInventories().add(inv1);
        warehouse.getInventories().add(inv2);

        // Act
        Integer result = inventoryService.OutBoundInventory(productId, warehouse, requestedQuantity);

        // Assert
        assertEquals(100, result); // 300 - 200 = 100 remaining
        assertEquals(0, inv1.getQtyOnHand());
        assertEquals(0, inv2.getQtyOnHand());
    }

    @Test
    @DisplayName("Should filter only matching product ID items")
    void testOutBoundInventoryFiltersCorrectProduct() {
        // Arrange
        Integer requestedQuantity = 50;
        UUID differentProductId = UUID.randomUUID();

        Product otherProduct = new Product();
        otherProduct.setId(differentProductId);

        Inventory correctInventory = new Inventory();
        correctInventory.setProduct(product);
        correctInventory.setQtyOnHand(100);

        Inventory wrongInventory = new Inventory();
        wrongInventory.setProduct(otherProduct);
        wrongInventory.setQtyOnHand(100);

        warehouse.getInventories().add(correctInventory);
        warehouse.getInventories().add(wrongInventory);

        // Act
        Integer result = inventoryService.OutBoundInventory(productId, warehouse, requestedQuantity);

        // Assert
        assertEquals(0, result);
        assertEquals(50, correctInventory.getQtyOnHand()); // Only correct product affected
        assertEquals(100, wrongInventory.getQtyOnHand()); // Wrong product unchanged
    }

    @Test
    @DisplayName("Should handle zero quantity request")
    void testOutBoundInventoryZeroQuantity() {
        // Arrange
        Integer requestedQuantity = 0;
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQtyOnHand(100);
        warehouse.getInventories().add(inventory);

        // Act
        Integer result = inventoryService.OutBoundInventory(productId, warehouse, requestedQuantity);

        // Assert
        assertEquals(0, result);
        assertEquals(100, inventory.getQtyOnHand()); // Unchanged
    }

    @Test
    @DisplayName("Should handle large quantity distribution")
    void testOutBoundInventoryLargeQuantity() {
        // Arrange
        Integer requestedQuantity = 1000;

        Inventory inv1 = new Inventory();
        inv1.setProduct(product);
        inv1.setQtyOnHand(300);

        Inventory inv2 = new Inventory();
        inv2.setProduct(product);
        inv2.setQtyOnHand(400);

        warehouse.getInventories().add(inv1);
        warehouse.getInventories().add(inv2);

        // Act
        Integer result = inventoryService.OutBoundInventory(productId, warehouse, requestedQuantity);

        // Assert
        assertEquals(300, result); // 1000 - 700 = 300 remaining
        assertEquals(0, inv1.getQtyOnHand());
        assertEquals(0, inv2.getQtyOnHand());
    }

    @Test
    @DisplayName("Should handle warehouse with empty inventory list")
    void testOutBoundInventoryEmptyWarehouse() {
        // Arrange
        Integer requestedQuantity = 100;

        // Act
        Integer result = inventoryService.OutBoundInventory(productId, warehouse, requestedQuantity);

        // Assert
        assertEquals(requestedQuantity, result);
    }
}
