package net.thesphynx.marketplace.controller;

import net.thesphynx.marketplace.dto.DeliveryDto;
import net.thesphynx.marketplace.enums.DeliveryStatus;
import net.thesphynx.marketplace.service.DeliveryIntegrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de demonstration de la communication SYNCHRONE marketplace -> delivery.
 * Touche uniquement marketplace : delivery est consomme via son API REST existante.
 */
@RestController
@RequestMapping("/mic2/deliveries")
public class DeliveryIntegrationController {

    private final DeliveryIntegrationService service;

    public DeliveryIntegrationController(DeliveryIntegrationService service) {
        this.service = service;
    }

    @PostMapping("/sync")
    public ResponseEntity<DeliveryDto> createSync(@RequestBody DeliveryDto dto) {
        return ResponseEntity.ok(service.createDeliverySync(dto));
    }

    @GetMapping("/sync/{id}")
    public ResponseEntity<DeliveryDto> getSync(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDeliverySync(id));
    }

    @GetMapping("/sync")
    public ResponseEntity<List<DeliveryDto>> listSync() {
        return ResponseEntity.ok(service.listDeliveriesSync());
    }

    @PatchMapping("/sync/{id}/status")
    public ResponseEntity<DeliveryDto> updateStatusSync(@PathVariable Long id,
                                                       @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(service.updateDeliveryStatusSync(id, status));
    }
}