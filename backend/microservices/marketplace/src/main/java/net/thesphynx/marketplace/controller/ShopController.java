package net.thesphynx.marketplace.controller;

import jakarta.validation.Valid;
import java.util.List;
import net.thesphynx.marketplace.dto.ShopRequest;
import net.thesphynx.marketplace.dto.ShopResponse;
import net.thesphynx.marketplace.entity.ShopStatus;
import net.thesphynx.marketplace.service.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mic2/shops")
public class ShopController {

    private final ShopService service;

    public ShopController(ShopService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShopResponse> create(@Valid @RequestBody ShopRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping
    public List<ShopResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ShopResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ShopResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ShopRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public ShopResponse changeStatus(
            @PathVariable Long id,
            @RequestParam ShopStatus status) {
        return service.changeStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}