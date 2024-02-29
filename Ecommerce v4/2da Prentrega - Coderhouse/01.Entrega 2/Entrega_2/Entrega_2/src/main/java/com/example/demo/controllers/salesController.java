package com.example.demo.controllers;

import com.example.demo.models.Sale;
import com.example.demo.models.SaleDetails;
import com.example.demo.services.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sales")
public class salesController {

    private final SaleService saleService;

    @Autowired
    public salesController(SaleService saleService) {
        this.saleService = saleService;
    }

    @Operation(summary = "Get all Sales", description = "Retrieve all sales.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @GetMapping("/get/all")
    public List<Sale> getAll() {
        return saleService.getAllSales();
    }

    @Operation(summary = "Get Sale by ID", description = "Retrieve a sale by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getSaleById(@PathVariable Long id) {
        Optional<Sale> foundSale = saleService.getSaleById(id);
        if (foundSale.isPresent()) {
            return ResponseEntity.ok(foundSale.get());
        } else {
            throw new GlobalExceptionHandler.ResourceNotFoundException("Sale not found with id: " + id);
        }
    }

    @Operation(summary = "Create Sale", description = "Create a new sale.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PostMapping("/add")
    public ResponseEntity<Sale> addSale(@RequestBody SaleDetails saleDetails) {
        Sale createdSale = saleService.createSale(saleDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSale);
    }

    @Operation(summary = "Generate Invoice by ID", description = "Generate an invoice by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @GetMapping("/invoice/{id}")
    public String generateInvoice(@PathVariable Long id) {
        return saleService.generateInvoice(id);
    }

    @Operation(summary = "Delete Sale by ID", description = "Delete a sale by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Sale> deleteSale(@PathVariable Long id) {
        Optional<Sale> deletedSale = saleService.deleteSale(id);
        return deletedSale.map(ResponseEntity::ok).orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("Sale not found with id: " + id));
    }

    @ControllerAdvice
    @RestController
    public static class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        // Clase para excepción personalizada
        public static class ResourceNotFoundException extends RuntimeException {
            public ResourceNotFoundException(String message) {
                super(message);
            }
        }
    }
}
