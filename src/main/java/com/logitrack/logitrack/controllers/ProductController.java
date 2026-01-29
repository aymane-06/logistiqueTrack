package com.logitrack.logitrack.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.logitrack.logitrack.dtos.Product.ProductDTO;
import com.logitrack.logitrack.dtos.Product.ProductRespDTO;
import com.logitrack.logitrack.services.ProductServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Products", description = "Product management endpoints for LogiTrack inventory system")
public class ProductController {
    private final ProductServices productServices;

    @PostMapping("/products")
    @Operation(summary = "Create a new product", description = "Creates a new product in the inventory system. Returns the created product with auto-generated SKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRespDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation errors"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductRespDTO> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductRespDTO savedProduct = productServices.saveProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/products")
    @Operation(summary = "Retrieve all products", description = "Fetches a list of all products currently available in the inventory system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRespDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<ProductRespDTO> getAllProducts() {
        return productServices.getAllProducts();
    }

    @GetMapping("/products/{sku}")
    @Operation(summary = "Get product by SKU", description = "Retrieves detailed information about a specific product using its SKU (Stock Keeping Unit).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRespDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found with the given SKU"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductRespDTO> getProductBySku(
            @Parameter(description = "Product SKU (Stock Keeping Unit)", example = "SKU-001")
            @PathVariable String sku) {
        ProductRespDTO product = productServices.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }
    @PutMapping("/products/{sku}")
    @Operation(summary = "Update an existing product", description = "Updates product information for a product identified by its SKU. Partial updates are supported.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRespDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation errors"),
            @ApiResponse(responseCode = "404", description = "Product not found with the given SKU"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductRespDTO> updateProduct(
            @Parameter(description = "Product SKU (Stock Keeping Unit)", example = "SKU-001")
            @PathVariable String sku,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductRespDTO productRespDTO = productServices.updateProduct(sku, productDTO);
        return ResponseEntity.status(HttpStatus.OK).body(productRespDTO);
    }

    @DeleteMapping("/products/{sku}")
    @Operation(summary = "Delete a product", description = "Permanently removes a product from the inventory system using its SKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found with the given SKU"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteProduct(
            @Parameter(description = "Product SKU (Stock Keeping Unit)", example = "SKU-001")
            @PathVariable String sku) {
        productServices.deleteProductBySku(sku);
        return ResponseEntity.status(HttpStatus.OK).body("Product with SKU: " + sku + " has been deleted.");
    }
}
