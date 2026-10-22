package net.thesphynx.marketplace.controller;

import jakarta.validation.Valid;
import java.util.List;
import net.thesphynx.marketplace.dto.ProductRequest;
import net.thesphynx.marketplace.dto.ProductResponse;
import net.thesphynx.marketplace.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping("/mic2/shops/{shopId}/products")
    public ResponseEntity<ProductResponse> create(
            @PathVariable Long shopId,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(shopId, request));
    }

    @GetMapping("/mic2/shops/{shopId}/products")
    public List<ProductResponse> findByShop(@PathVariable Long shopId) {
        return service.findByShop(shopId);
    }

    @GetMapping("/mic2/products")
    public List<ProductResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/mic2/products/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/mic2/products/{id}")
    public ProductResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/mic2/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}