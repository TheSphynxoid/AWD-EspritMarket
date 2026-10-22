package net.thesphynx.delivery.controller;

import jakarta.validation.Valid;
import net.thesphynx.delivery.entity.Deliverer;
import net.thesphynx.delivery.service.DelivererService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/deliverers")
public class DelivererController {

    private final DelivererService delivererService;

    public DelivererController(DelivererService delivererService) {
        this.delivererService = delivererService;
    }

    @GetMapping
    public List<Deliverer> findAll() {
        return delivererService.findAll();
    }

    @GetMapping("/{id}")
    public Deliverer findById(@PathVariable Long id) {
        return delivererService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Deliverer> create(@Valid @RequestBody Deliverer deliverer) {
        Deliverer created = delivererService.create(deliverer);
        return ResponseEntity.created(URI.create("/api/deliverers/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public Deliverer update(@PathVariable Long id, @Valid @RequestBody Deliverer deliverer) {
        return delivererService.update(id, deliverer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        delivererService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
