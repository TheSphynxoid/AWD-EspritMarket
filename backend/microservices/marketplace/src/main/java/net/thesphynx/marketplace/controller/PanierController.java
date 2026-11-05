package net.thesphynx.marketplace.controller;

import jakarta.validation.Valid;
import net.thesphynx.marketplace.dto.AjouterAuPanierRequest;
import net.thesphynx.marketplace.dto.PanierResponse;
import net.thesphynx.marketplace.dto.ModifierLignePanierRequest;
import net.thesphynx.marketplace.service.PanierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PanierController {
    private final PanierService service;

    public PanierController(PanierService service) {
        this.service = service;
    }

    @GetMapping("/mic2/paniers/{userId}")
    public PanierResponse getPanier(@PathVariable Long userId) {
        return service.getPanier(userId);
    }

    @PostMapping("/mic2/paniers/{userId}/items")
    public PanierResponse addItem(@PathVariable Long userId,
                                @Valid @RequestBody AjouterAuPanierRequest request) {
        return service.addItem(userId, request);
    }

    @PutMapping("/mic2/paniers/{userId}/items/{productId}")
    public PanierResponse updateItem(@PathVariable Long userId,
                                   @PathVariable Long productId,
                                   @Valid @RequestBody ModifierLignePanierRequest request) {
        return service.updateItem(userId, productId, request);
    }

    @DeleteMapping("/mic2/paniers/{userId}/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long userId, @PathVariable Long productId) {
        service.removeItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/mic2/paniers/{userId}")
    public ResponseEntity<Void> clearPanier(@PathVariable Long userId) {
        service.clearPanier(userId);
        return ResponseEntity.noContent().build();
    }
}
