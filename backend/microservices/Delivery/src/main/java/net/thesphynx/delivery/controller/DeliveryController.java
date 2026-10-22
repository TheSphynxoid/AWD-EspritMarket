package net.thesphynx.delivery.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import net.thesphynx.delivery.entity.Delivery;
import net.thesphynx.delivery.enums.DeliveryStatus;
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

    /** Corps de PATCH /{id}/status : {"status": "IN_PROGRESS"}. */
    public record StatusRequest(@NotNull(message = "Le statut est obligatoire") DeliveryStatus status) {
    }

    @GetMapping
    public List<Delivery> findAll() {
        return deliveryService.findAll();
    }

    @GetMapping("/{id}")
    public Delivery findById(@PathVariable Long id) {
        return deliveryService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Delivery> create(@Valid @RequestBody Delivery delivery) {
        Delivery created = deliveryService.create(delivery);
        return ResponseEntity.created(URI.create("/api/deliveries/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public Delivery update(@PathVariable Long id, @Valid @RequestBody Delivery delivery) {
        return deliveryService.update(id, delivery);
    }

    @PatchMapping("/{id}/status")
    public Delivery updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return deliveryService.updateStatus(id, request.status());
    }

    @PutMapping("/{id}/deliverer/{delivererId}")
    public Delivery assignDeliverer(@PathVariable Long id, @PathVariable Long delivererId) {
        return deliveryService.assignDeliverer(id, delivererId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deliveryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
