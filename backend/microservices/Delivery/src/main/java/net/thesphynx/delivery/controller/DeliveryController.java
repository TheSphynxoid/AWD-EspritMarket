package net.thesphynx.delivery.controller;

import jakarta.validation.Valid;
import net.thesphynx.delivery.dto.DeliveryDto;
import net.thesphynx.delivery.dto.DeliveryStatusDto;
import net.thesphynx.delivery.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public List<DeliveryDto> findAll() {
        return deliveryService.findAll();
    }

    @GetMapping("/{id}")
    public DeliveryDto findById(@PathVariable Long id) {
        return deliveryService.findById(id);
    }

    @PostMapping
    public ResponseEntity<DeliveryDto> create(@Valid @RequestBody DeliveryDto dto) {
        DeliveryDto created = deliveryService.create(dto);
        return ResponseEntity.created(URI.create("/api/deliveries/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public DeliveryDto update(@PathVariable Long id, @Valid @RequestBody DeliveryDto dto) {
        return deliveryService.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public DeliveryDto updateStatus(@PathVariable Long id, @Valid @RequestBody DeliveryStatusDto dto) {
        return deliveryService.updateStatus(id, dto.getStatus());
    }

    @PutMapping("/{id}/deliverer/{delivererId}")
    public DeliveryDto assignDeliverer(@PathVariable Long id, @PathVariable Long delivererId) {
        return deliveryService.assignDeliverer(id, delivererId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deliveryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
