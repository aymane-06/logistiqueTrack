package com.logitrack.logitrack.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logitrack.logitrack.mapper.PurchaseOrderMapper;
import com.logitrack.logitrack.mapper.SalesOrderMapper;
import com.logitrack.logitrack.repositories.CarrierRepository;
import com.logitrack.logitrack.repositories.SalesOrderRepository;
import com.logitrack.logitrack.repositories.WarehouseRepository;

@ExtendWith(MockitoExtension.class)
public class SalesOrderServicesTest {
    // Test methods to be implemented
    @Mock
    private  SalesOrderRepository salesOrderRepository;
    @Mock
    private  SalesOrderMapper salesOrderMapper;
    @Mock
    private  WarehouseRepository warehouseRepository;
    @Mock
    private  PurchaseOrderMapper purchaseOrderMapper;
    @Mock
    private  CarrierRepository carrierRepository;

    @InjectMocks
    private SalesOrderService salesOrderService;
    
    
    @BeforeEach
    void setUp() {
        // Initialization before each test if needed
    }
}
