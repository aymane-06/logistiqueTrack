package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.Product.ProductDTO;
import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.services.ProductServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {
    private final ProductServices productServices;

    @PostMapping("/products/add")
    public ResponseEntity<ProductRespDTO> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductRespDTO savedProduct = productServices.saveProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/products")
    public List<ProductRespDTO> getAllProducts() {
        return productServices.getAllProducts();
    }

    @GetMapping("/products/{sku}")
    public ResponseEntity<ProductRespDTO> getProductBySku(@PathVariable String sku) {
        ProductRespDTO product = productServices.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }
    @PutMapping("/products/update/{sku}")
    public ResponseEntity<ProductRespDTO> updateProduct(@PathVariable String sku, @Valid @RequestBody ProductDTO productDTO) {
        ProductRespDTO productRespDTO = productServices.updateProduct(sku, productDTO);
        return ResponseEntity.status(HttpStatus.OK).body(productRespDTO);
    }

    @DeleteMapping("/products/delete/{sku}")
    public ResponseEntity<String> deleteProduct(@PathVariable String sku) {
        productServices.deleteProductBySku(sku);
        return ResponseEntity.status(HttpStatus.OK).body("Product with SKU: " + sku + " has been deleted.");
    }
}
