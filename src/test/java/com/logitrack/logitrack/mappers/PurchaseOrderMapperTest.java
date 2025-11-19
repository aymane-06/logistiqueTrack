package com.logitrack.logitrack.mappers;

import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderLine.PurchaseOrderLineDTO;
import com.logitrack.logitrack.mapper.PurchaseOrderMapper;
import com.logitrack.logitrack.mapper.PurchaseOrderMapperImpl;
import com.logitrack.logitrack.models.*;
import com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus;
import com.logitrack.logitrack.repositories.ProductRepository;
import com.logitrack.logitrack.repositories.SupplierRepository;
import com.logitrack.logitrack.repositories.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseOrderMapperTest {
    // Dependencies to Mock
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private ProductRepository productRepository;

    // The mapper under test
    @InjectMocks
    private PurchaseOrderMapperImpl purchaseOrderMapper;

    // Test Data
    private UUID supplierId = UUID.randomUUID();
    private UUID warehouseId = UUID.randomUUID();
    private UUID productId = UUID.randomUUID();
    private Product product;
    private PurchaseOrderDTO purchaseOrderDTO;
    private PurchaseOrderLineDTO purchaseOrderLineDTO;
    private PurchaseOrder purchaseOrder;
    private PurchaseOrderLine purchaseOrderLine;
    private Supplier supplier;
    private Warehouse warehouse;


    @BeforeEach
    void setUp() {
        // Initialize test data
        supplier = Supplier.builder()
                .id(supplierId)
                .name("Test Supplier")
                .build();

        warehouse = Warehouse.builder()
                .id(warehouseId)
                .name("Test Warehouse")
                .build();

        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .active(true)
                .build();

        purchaseOrderLineDTO = PurchaseOrderLineDTO.builder()
                .productId(productId)
                .quantity(10)
                .unitPrice(java.math.BigDecimal.valueOf(100))
                .build();

        purchaseOrderDTO = PurchaseOrderDTO.builder()
                .supplierId(supplierId)
                .warehouseId(warehouseId)
                .expectedDelivery(java.time.LocalDateTime.now().plusDays(5))
                .status(PurchaseOrderStatus.CREATED)
                .lines(java.util.List.of(purchaseOrderLineDTO))
                .build();

        purchaseOrderLine = PurchaseOrderLine.builder()
                .product(product)
                .quantity(10)
                .unitPrice(java.math.BigDecimal.valueOf(100))
                .build();

        purchaseOrder = PurchaseOrder.builder()
                .supplier(supplier)
                .warehouse(warehouse)
                .expectedDelivery(purchaseOrderDTO.getExpectedDelivery())
                .lines(List.of(purchaseOrderLine))
                .status(purchaseOrderDTO.getStatus())
                .build();
    }

    @Test
    @DisplayName("Test mapping from PurchaseOrderDTO to PurchaseOrder entity")
    void testToPurchaseOrder() {
        //arrange
            when(supplierRepository.findById(supplierId)).thenReturn(java.util.Optional.of(supplier));
            when(warehouseRepository.findById(warehouseId)).thenReturn(java.util.Optional.of(warehouse));
            when(productRepository.findByIdAndActive(productId, true)).thenReturn(java.util.Optional.of(product));
        //act
            PurchaseOrder mappedOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        //assert
        assertThat(mappedOrder).isNotNull();
        assertThat(mappedOrder.getSupplier()).isEqualTo(supplier);
        assertThat(mappedOrder.getWarehouse()).isEqualTo(warehouse);
        assertThat(mappedOrder.getExpectedDelivery()).isEqualTo(purchaseOrderDTO.getExpectedDelivery());
        assertThat(mappedOrder.getStatus()).isEqualTo(purchaseOrderDTO.getStatus());
        assertThat(mappedOrder.getLines()).hasSize(1);
        PurchaseOrderLine mappedLine = mappedOrder.getLines().get(0);
        assertThat(mappedLine.getProduct()).isEqualTo(product);
        assertThat(mappedLine.getQuantity()).isEqualTo(purchaseOrderLineDTO.getQuantity());
        assertThat(mappedLine.getUnitPrice()).isEqualTo(purchaseOrderLineDTO.getUnitPrice());
        //verify
        verify(supplierRepository,times(1)).findById(supplierId);

    }

    @Test
    @DisplayName("Test mapping from PurchaseOrderDTO to PurchaseOrder entity should throw exception when Supplier not found")
    void testToPurchaseOrder_SupplierNotFound() {
        //arrange
        when(supplierRepository.findById(supplierId)).thenReturn(java.util.Optional.empty());
        //act & assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> purchaseOrderMapper.toEntity(purchaseOrderDTO),
                "Expected IllegalArgumentException to be thrown"
        );

        assertThat(exception.getMessage()).isEqualTo("Supplier with id " + supplierId + " not found.");

        verify(supplierRepository,times(1)).findById(supplierId);
        verify(warehouseRepository,never()).findById(any());


    }





}
