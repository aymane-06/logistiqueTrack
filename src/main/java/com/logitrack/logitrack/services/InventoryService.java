package com.logitrack.logitrack.services;

import com.logitrack.logitrack.models.Inventory;
import com.logitrack.logitrack.models.InventoryMovement;
import com.logitrack.logitrack.models.Warehouse;
import com.logitrack.logitrack.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.logitrack.logitrack.models.ENUM.MovementType.OUTBOUND;

@Service
@RequiredArgsConstructor
public class InventoryService {



    public Integer OutBoundInventory(UUID productId, Warehouse warehouse, Integer quantity) {
        List<Inventory> inventory = warehouse.getInventories();

        List<Inventory> inventoryItems = inventory.stream()
                .filter(inv -> inv.getProduct().getId().equals(productId))
                .toList();
        if (inventoryItems.isEmpty()) {
            return quantity;
        }

        int[] remainingQuantity = {quantity};
        inventoryItems.forEach(inv -> {
            if (inv.getQtyOnHand() >= remainingQuantity[0]) {
                inv.setQtyOnHand(inv.getQtyOnHand() - remainingQuantity[0]);

                remainingQuantity[0] = 0;
            } else {
                remainingQuantity[0] -= inv.getQtyOnHand();
                inv.setQtyOnHand(0);
            }
            InventoryMovement inventoryMovement = InventoryMovement.builder()
                    .inventory(inv)
                    .type(OUTBOUND)
                    .quantity(remainingQuantity[0])
                    .build();
        });
        return remainingQuantity[0];
    }

    
}
