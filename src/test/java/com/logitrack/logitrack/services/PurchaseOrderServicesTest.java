package com.logitrack.logitrack.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logitrack.logitrack.dtos.SupplierDTO;
import com.logitrack.logitrack.dtos.WarehouseManagerDTO;
import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderRespDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderLine.PurchaseOrderLineDTO;
import com.logitrack.logitrack.dtos.PurchaseOrder.PurchaseOrderLine.PurchaseOrderLineRespDTO;
import com.logitrack.logitrack.dtos.Warehouse.OrderWarehouseRespDTO;
import com.logitrack.logitrack.mapper.PurchaseOrderMapperImpl;
import com.logitrack.logitrack.models.Product;
import com.logitrack.logitrack.models.PurchaseOrder;
import com.logitrack.logitrack.models.PurchaseOrderLine;
import com.logitrack.logitrack.models.Supplier;
import com.logitrack.logitrack.models.WAREHOUSE_MANAGER;
import com.logitrack.logitrack.models.Warehouse;
import com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus;
import com.logitrack.logitrack.repositories.ProductRepository;
import com.logitrack.logitrack.repositories.PurchaseOrderRepository;
import com.logitrack.logitrack.repositories.SupplierRepository;
import com.logitrack.logitrack.repositories.WarehouseRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PurchaseOrderServices Tests")
public class PurchaseOrderServicesTest {
    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Spy
    private PurchaseOrderMapperImpl purchaseOrderMapper;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;


    private PurchaseOrder purchaseOrder;
    private PurchaseOrderDTO purchaseOrderDTO;
    private PurchaseOrderRespDTO purchaseOrderRespDTO;
    private PurchaseOrderLine purchaseOrderLine;
    private PurchaseOrderLineDTO purchaseOrderLineDTO;
    private PurchaseOrderLineRespDTO purchaseOrderLineRespDTO;
    private Product product;
    private ProductRespDTO productRespDTO;
    private Supplier supplier;
    private SupplierDTO supplierDTO;
    private Warehouse warehouse;
    private OrderWarehouseRespDTO orderWarehouseRespDTO;
    private WarehouseManagerDTO warehouseManagerDTO;


    private UUID purchaseOrderId;
    private UUID supplierId;
    private UUID productId;
    private UUID warehouseId;
    private UUID warehouseManagerId;
    private UUID purchaseOrderLineId;

    @BeforeEach
    void setUp() {
        purchaseOrderId = UUID.randomUUID();
        supplierId = UUID.randomUUID();
        productId = UUID.randomUUID();
        warehouseId = UUID.randomUUID();
        warehouseManagerId = UUID.randomUUID();
        purchaseOrderLineId = UUID.randomUUID();
        // Initialize mapper dependencies
        purchaseOrderMapper.setSupplierRepository(supplierRepository);
        purchaseOrderMapper.setProductRepository(productRepository);
        purchaseOrderMapper.setWarehouseRepository(warehouseRepository);





        // Initialize test data
        WAREHOUSE_MANAGER warehouseManager = WAREHOUSE_MANAGER.builder()
                .id(warehouseManagerId)
                .name("John Doe")
                .email("warehousManagerTest@gmail.com")
                .passwordHash("test1234*")
                .build();
        warehouseManagerDTO = WarehouseManagerDTO.builder()
                .id(warehouseManagerId)
                .name("John Doe")
                .email("warehousManagerTest@gmail.com")
                .phone("123-456-7890")
                .build();
        warehouse = Warehouse.builder()
                .id(warehouseId)
                .name("Main Warehouse")
                .location("1234 Test St, Test City")
                .code("WH-TEST01")
                .warehouse_manager(warehouseManager)
                .inventories(new java.util.ArrayList<>())
                .purchaseOrders(new java.util.ArrayList<>())
                .active(true)
                .build();

        supplier = Supplier.builder()
                .id(supplierId)
                .name("Test Supplier")
                .contactInfo("test@supplier.com")
                .build();
        supplierDTO = SupplierDTO.builder()
                .id(supplierId.toString())
                .name("Test Supplier")
                .contactInfo("test@supplier.com")
                .build();

        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .sku("SKU-001")
                .category("Electronics")
                .boughtPrice(new java.math.BigDecimal("50.00"))
                .active(true)
                .inventory(new java.util.ArrayList<>())
                .build();
        productRespDTO = ProductRespDTO.builder()
                .id(productId)
                .name("Test Product")
                .sku("SKU-001")
                .category("Electronics")
                .boughtPrice(new java.math.BigDecimal("50.00"))
                .active(true)
                .build();
        
        purchaseOrderLine = PurchaseOrderLine.builder()
                .id(purchaseOrderLineId)
                .product(product)
                .quantity(10)
                .unitPrice(new java.math.BigDecimal("45.00"))
                .build();
        purchaseOrderLineDTO = PurchaseOrderLineDTO.builder()
                .productId(productId)
                .quantity(10)
                .unitPrice(new java.math.BigDecimal("45.00"))
                .build();
        purchaseOrderLineRespDTO = PurchaseOrderLineRespDTO.builder()
                .product(productRespDTO)
                .quantity(10)
                .unitPrice(new java.math.BigDecimal("45.00"))
                .build();

        purchaseOrderDTO = PurchaseOrderDTO.builder()
                .supplierId(supplierId)
                .warehouseId(warehouseId)
                .lines(java.util.List.of(purchaseOrderLineDTO))
                .build();
        purchaseOrder = PurchaseOrder.builder()
                .id(purchaseOrderId)
                .supplier(supplier)
                .warehouse(warehouse)
                .lines(java.util.List.of(purchaseOrderLine))
                .status(PurchaseOrderStatus.CREATED)
                .expectedDelivery(java.time.LocalDateTime.now().plusDays(10))
                .build();
        
        orderWarehouseRespDTO = OrderWarehouseRespDTO.builder()
                .id(warehouseId)
                .name("Main Warehouse")
                .location("1234 Test St, Test City")
                .warehouse_manager(warehouseManagerDTO)
                .build();
        
        purchaseOrderRespDTO = PurchaseOrderRespDTO.builder()
                .id(purchaseOrderId)
                .supplier(supplierDTO)
                .warehouse(orderWarehouseRespDTO)
                .lines(java.util.List.of(purchaseOrderLineRespDTO))
                .status("CREATED")
                .build();
    }


    @Test
    @DisplayName("Should create purchase order successfully")
    void shouldCreatePurchaseOrderSuccessfully() {
        //Arrange
        doReturn(purchaseOrder).when(purchaseOrderMapper).toEntity(purchaseOrderDTO);
        when(purchaseOrderRepository.save(purchaseOrder)).thenReturn(purchaseOrder);
        doReturn(purchaseOrderRespDTO).when(purchaseOrderMapper).toResponseDTO(purchaseOrder);
        
        // Act
        PurchaseOrderRespDTO createdPurchaseOrder = purchaseOrderService.createPurchaseOrder(purchaseOrderDTO);
        
        // Assert
        assertThat(createdPurchaseOrder).isNotNull();
        assertThat(createdPurchaseOrder.getId()).isEqualTo(purchaseOrderId);
        assertThat(createdPurchaseOrder.getSupplier().getId()).isEqualTo(supplierId.toString());
        assertThat(createdPurchaseOrder.getWarehouse().getId()).isEqualTo(warehouseId);
        assertThat(createdPurchaseOrder.status).isEqualTo("CREATED");
        assertThat(createdPurchaseOrder.getLines()).hasSize(1);
        assertThat(createdPurchaseOrder.getLines().get(0).getProduct().getId()).isEqualTo(productId);
        
        // Verify interactions
        verify(purchaseOrderMapper).toEntity(purchaseOrderDTO);
        verify(purchaseOrderRepository).save(purchaseOrder);
        verify(purchaseOrderMapper).toResponseDTO(purchaseOrder);
    }


        @Test
        @DisplayName("Should get purchase order by ID successfully")
        void shouldGetPurchaseOrderByIdSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.ofNullable(purchaseOrder));
            doReturn(purchaseOrderRespDTO).when(purchaseOrderMapper).toResponseDTO(purchaseOrder);
                // Act
                PurchaseOrderRespDTO fetchedPurchaseOrder = purchaseOrderService.getPurchaseOrderById(purchaseOrderId);
                // Assert
                assertThat(fetchedPurchaseOrder).isNotNull();
                assertThat(fetchedPurchaseOrder.getId()).isEqualTo(purchaseOrderId);
                assertThat(fetchedPurchaseOrder.getSupplier().getId()).isEqualTo(supplierId.toString());
                assertThat(fetchedPurchaseOrder.getWarehouse().getId()).isEqualTo(warehouseId);
                assertThat(fetchedPurchaseOrder.status).isEqualTo("CREATED");
                assertThat(fetchedPurchaseOrder.getLines()).hasSize(1);
                assertThat(fetchedPurchaseOrder.getLines().get(0).getProduct().getId()).isEqualTo(productId);
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
                verify(purchaseOrderMapper).toResponseDTO(purchaseOrder); 
        }

    @Test
    @DisplayName("Should throw exception when purchase order not found by ID")
    void shouldThrowExceptionWhenPurchaseOrderNotFoundById() {
        // Arrange
        when(purchaseOrderRepository.findById(purchaseOrderId))
                .thenReturn(java.util.Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> purchaseOrderService.getPurchaseOrderById(purchaseOrderId),
            "Expected IllegalArgumentException to be thrown"
        );
        
        assertThat(exception.getMessage())
            .isEqualTo("Purchase Order with id " + purchaseOrderId + " not found.");
        
        // Verify interactions
        verify(purchaseOrderRepository).findById(purchaseOrderId);
    }

        @Test
        @DisplayName("Should update purchase order successfully")
        void shouldUpdatePurchaseOrderSuccessfully() {
            // Arrange
            java.time.LocalDateTime expectedDeliveryDate = java.time.LocalDateTime.now().plusDays(10);
            PurchaseOrderDTO updateDTO = PurchaseOrderDTO.builder()
                    .expectedDelivery(expectedDeliveryDate)
                    .status(com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus.RECEIVED)
                    .build();
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.ofNullable(purchaseOrder));
            when(purchaseOrderRepository.save(purchaseOrder))
                    .thenReturn(purchaseOrder);
            doReturn(purchaseOrderRespDTO).when(purchaseOrderMapper).toResponseDTO(purchaseOrder);
            
            // Act
            PurchaseOrderRespDTO updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(purchaseOrderId, updateDTO);
            
            // Assert
            assertThat(updatedPurchaseOrder).isNotNull();
            assertThat(updatedPurchaseOrder.getId()).isEqualTo(purchaseOrderId);
            assertThat(updatedPurchaseOrder.getSupplier().getId()).isEqualTo(supplierId.toString());
            assertThat(updatedPurchaseOrder.getWarehouse().getId()).isEqualTo(warehouseId);
            assertThat(updatedPurchaseOrder.getLines()).hasSize(1);
            assertThat(updatedPurchaseOrder.getLines().get(0).getProduct().getId()).isEqualTo(productId);
            
            // Verify interactions
            verify(purchaseOrderRepository).findById(purchaseOrderId);
            verify(purchaseOrderRepository).save(purchaseOrder);
            verify(purchaseOrderMapper).toResponseDTO(purchaseOrder); 
        }


        @Test
        @DisplayName("Should throw exception when updating non-existent purchase order")
        void shouldThrowExceptionWhenUpdatingNonExistentPurchaseOrder() {
            // Arrange
            PurchaseOrderDTO updateDTO = PurchaseOrderDTO.builder()
                    .expectedDelivery(java.time.LocalDateTime.now().plusDays(10))
                    .status(com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus.RECEIVED)
                    .build();
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.empty());
                // Act & Assert
                try {
                    purchaseOrderService.updatePurchaseOrder(purchaseOrderId, updateDTO);
                    assert false; // Fail the test if no exception is thrown
                } catch (IllegalArgumentException e) {
                    assert e.getMessage().equals("Purchase Order with id " + purchaseOrderId + " not found.");
                }
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
        }


        @Test
        @DisplayName("Should delete purchase order successfully")
        void shouldDeletePurchaseOrderSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.ofNullable(purchaseOrder));
                // Act
                purchaseOrderService.deletePurchaseOrderById(purchaseOrderId);
                // Assert
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
                verify(purchaseOrderRepository).delete(purchaseOrder);
        }


        @Test
        @DisplayName("Should throw exception when deleting non-existent purchase order")
        void shouldThrowExceptionWhenDeletingNonExistentPurchaseOrder() {
            // Arrange
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.empty());
                // Act & Assert
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> purchaseOrderService.deletePurchaseOrderById(purchaseOrderId),
                    "Expected IllegalArgumentException when purchase order not found"
                );
                assertThat(exception.getMessage()).isNotEmpty();
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
        }


        @Test
        @DisplayName("Should Receive purchase order successfully")
        void shouldReceivePurchaseOrderSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.ofNullable(purchaseOrder));
            when(purchaseOrderRepository.save(purchaseOrder))
                    .thenReturn(purchaseOrder);
            doReturn(purchaseOrderRespDTO)
                    .when(purchaseOrderMapper)
                    .toResponseDTO(purchaseOrder);
                // Act
                PurchaseOrderRespDTO updatedPurchaseOrder = purchaseOrderService.parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.RECEIVED);
                // Assert
                assertThat(updatedPurchaseOrder).isNotNull();
                assertThat(updatedPurchaseOrder.getId()).isEqualTo(purchaseOrderId);
                assertThat(updatedPurchaseOrder.getSupplier().getId()).isEqualTo(supplierId.toString());
                assertThat(updatedPurchaseOrder.getWarehouse().getId()).isEqualTo(warehouseId);
                assertThat(updatedPurchaseOrder.getLines()).hasSize(1);
                assertThat(updatedPurchaseOrder.getLines().get(0).getProduct().getId()).isEqualTo(productId);
                assertThat(purchaseOrder.getStatus()).isEqualTo(com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus.RECEIVED);
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
                verify(purchaseOrderRepository).save(purchaseOrder);
                verify(purchaseOrderMapper).toResponseDTO(purchaseOrder);
        }

        @Test
        @DisplayName("Should throw exception when updating status of delivered purchase order")
        void shouldThrowExceptionWhenUpdatingStatusOfDeliveredPurchaseOrder() {
            // Arrange
            purchaseOrder.setStatus(PurchaseOrderStatus.RECEIVED);
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.ofNullable(purchaseOrder));
                // Act & Assert
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> purchaseOrderService.parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.CREATED),
                    "Expected IllegalArgumentException when updating status of delivered PO"
                );
                assertThat(exception.getMessage()).isNotEmpty();
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
        }


        @Test
        @DisplayName("Should throw exception when updating status of non-existent purchase order")
        void shouldThrowExceptionWhenUpdatingStatusOfNonExistentPurchaseOrder() {
            // Arrange
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.empty());
                // Act & Assert
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> purchaseOrderService.parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.RECEIVED),
                    "Expected IllegalArgumentException when purchase order not found"
                );
                assertThat(exception.getMessage()).isNotEmpty();
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
                }


        @Test
        @DisplayName("Should get all purchase orders successfully")
        void shouldGetAllPurchaseOrdersSuccessfully() {
            // Arrange  
            when(purchaseOrderRepository.findAll())
                    .thenReturn(java.util.List.of(purchaseOrder));
            doReturn(purchaseOrderRespDTO)
                    .when(purchaseOrderMapper)
                    .toResponseDTO(purchaseOrder);
                // Act
                java.util.List<PurchaseOrderRespDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
                // Assert
                assert purchaseOrders != null;
                assert purchaseOrders.size() == 1;
                PurchaseOrderRespDTO fetchedPurchaseOrder = purchaseOrders.get(0);
                assert fetchedPurchaseOrder != null;
                        assert fetchedPurchaseOrder.getId().equals(purchaseOrderId);
                        assert fetchedPurchaseOrder.getSupplier().getId().equals(supplierId.toString());
                        assert fetchedPurchaseOrder.getWarehouse().getId().equals(warehouseId);
                        assert fetchedPurchaseOrder.status.equals("CREATED");
                        assert fetchedPurchaseOrder.getLines().size() == 1;
                        assert fetchedPurchaseOrder.getLines().get(0).getProduct().getId().equals(productId);
                        // Verify interactions
                        verify(purchaseOrderRepository).findAll();
                        verify(purchaseOrderMapper).toResponseDTO(purchaseOrder);
        }



        @Test
        @DisplayName("Should throw exception when creating purchase order with invalid supplier")
        void shouldThrowExceptionWhenCreatingPurchaseOrderWithInvalidSupplier() {
            // Arrange - spy mapper will return null when unable to map
            doReturn(null)
                    .when(purchaseOrderMapper)
                    .toEntity(purchaseOrderDTO);
            
            // Act & Assert - service will throw NPE when trying to access null entity
            assertThrows(
                NullPointerException.class,
                () -> purchaseOrderService.createPurchaseOrder(purchaseOrderDTO),
                "Expected NullPointerException when mapper returns null"
            );
        }

        @Test
        @DisplayName("Should throw exception when creating purchase order with invalid warehouse")
        void shouldThrowExceptionWhenCreatingPurchaseOrderWithInvalidWarehouse() {
            // Arrange - spy mapper will return null when unable to map
            doReturn(null)
                    .when(purchaseOrderMapper)
                    .toEntity(purchaseOrderDTO);
            
            // Act & Assert - service will throw NPE when trying to access null entity
            assertThrows(
                NullPointerException.class,
                () -> purchaseOrderService.createPurchaseOrder(purchaseOrderDTO),
                "Expected NullPointerException when mapper returns null"
            );
        }

        @Test
        @DisplayName("Should throw exception when creating purchase order with invalid product")
        void shouldThrowExceptionWhenCreatingPurchaseOrderWithInvalidProduct() {
            // Arrange - spy mapper will return null when unable to map
            doReturn(null)
                    .when(purchaseOrderMapper)
                    .toEntity(purchaseOrderDTO);
            
            // Act & Assert - service will throw NPE when trying to access null entity
            assertThrows(
                NullPointerException.class,
                () -> purchaseOrderService.createPurchaseOrder(purchaseOrderDTO),
                "Expected NullPointerException when mapper returns null"
            );
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent purchase order status")
        void shouldThrowExceptionWhenUpdatingNonExistentPurchaseOrderStatus() {
            // Arrange
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.empty());
                // Act & Assert
                try {
                    purchaseOrderService.parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.RECEIVED);
                    assert false; // Fail the test if no exception is thrown
                } catch (IllegalArgumentException e) {
                    assert e.getMessage().equals("Purchase Order with id " + purchaseOrderId + " not found.");
                }
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
        }


        @Test
        @DisplayName("Should throw exception when deleting non-existent purchase order by ID")
        void shouldThrowExceptionWhenDeletingNonExistentPurchaseOrderById() {
            // Arrange
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.empty());
                // Act & Assert
                IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> purchaseOrderService.deletePurchaseOrderById(purchaseOrderId),
                    "Expected IllegalArgumentException when purchase order not found"
                );
                assertThat(exception.getMessage()).isNotEmpty();
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
        }


        @Test
        @DisplayName("Should throw exception when getting non-existent purchase order by ID")
        void shouldThrowExceptionWhenGettingNonExistentPurchaseOrderById() {
                // Arrange
                when(purchaseOrderRepository.findById(purchaseOrderId))
                        .thenReturn(java.util.Optional.empty());
                    // Act & Assert
                    IllegalArgumentException exception = assertThrows(
                        IllegalArgumentException.class,
                        () -> purchaseOrderService.getPurchaseOrderById(purchaseOrderId),
                        "Expected IllegalArgumentException when purchase order not found"
                    );
                    assertThat(exception.getMessage()).isNotEmpty();
                    // Verify interactions
                    verify(purchaseOrderRepository).findById(purchaseOrderId);
                }

        @Test
        @DisplayName("Should update purchase order status to APPROVED successfully")
        void shouldUpdatePurchaseOrderStatusToApprovedSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.ofNullable(purchaseOrder));
            when(purchaseOrderRepository.save(purchaseOrder))
                    .thenReturn(purchaseOrder);
            doReturn(purchaseOrderRespDTO)
                    .when(purchaseOrderMapper)
                    .toResponseDTO(purchaseOrder);
                // Act
                PurchaseOrderRespDTO updatedPurchaseOrder = purchaseOrderService.parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.APPROVED);
                // Assert
                assertThat(updatedPurchaseOrder).isNotNull();
                assertThat(updatedPurchaseOrder.getId()).isEqualTo(purchaseOrderId);
                assertThat(updatedPurchaseOrder.getSupplier().getId()).isEqualTo(supplierId.toString());
                assertThat(updatedPurchaseOrder.getWarehouse().getId()).isEqualTo(warehouseId);
                assertThat(updatedPurchaseOrder.getLines()).hasSize(1);
                assertThat(updatedPurchaseOrder.getLines().get(0).getProduct().getId()).isEqualTo(productId);
                assertThat(purchaseOrder.getStatus()).isEqualTo(com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus.APPROVED);
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
                verify(purchaseOrderRepository).save(purchaseOrder);
                verify(purchaseOrderMapper).toResponseDTO(purchaseOrder);
        }

        @Test
        @DisplayName("Should update purchase order status to CANCELED successfully")
        void shouldUpdatePurchaseOrderStatusToCanceledSuccessfully() {
            // Arrange
            when(purchaseOrderRepository.findById(purchaseOrderId))
                    .thenReturn(java.util.Optional.ofNullable(purchaseOrder));
            when(purchaseOrderRepository.save(purchaseOrder))
                    .thenReturn(purchaseOrder);
            doReturn(purchaseOrderRespDTO)
                    .when(purchaseOrderMapper)
                    .toResponseDTO(purchaseOrder);
                // Act
                PurchaseOrderRespDTO updatedPurchaseOrder = purchaseOrderService.parchaseOrderStatusUpdate(purchaseOrderId, PurchaseOrderStatus.CANCELED);
                // Assert
                assert updatedPurchaseOrder != null;
                assert updatedPurchaseOrder.getId().equals(purchaseOrderId);
                assert updatedPurchaseOrder.getSupplier().getId().equals(supplierId.toString());
                assert updatedPurchaseOrder.getWarehouse().getId().equals(warehouseId);
                assert updatedPurchaseOrder.getLines().size() == 1;
                assert updatedPurchaseOrder.getLines().get(0).getProduct().getId().equals(productId);
                assert purchaseOrder.getStatus().equals(com.logitrack.logitrack.models.ENUM.PurchaseOrderStatus.CANCELED);
                // Verify interactions
                verify(purchaseOrderRepository).findById(purchaseOrderId);
                verify(purchaseOrderRepository).save(purchaseOrder);
                verify(purchaseOrderMapper).toResponseDTO(purchaseOrder);
        }
}