package net.thesphynx.delivery.controller;

import jakarta.validation.Valid;
import net.thesphynx.delivery.dto.DelivererDto;
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
    public List<DelivererDto> findAll() {
        return delivererService.findAll();
    }

    @GetMapping("/{id}")
    public DelivererDto findById(@PathVariable Long id) {
        return delivererService.findById(id);
    }

    @PostMapping
    public ResponseEntity<DelivererDto> create(@Valid @RequestBody DelivererDto dto) {
        DelivererDto created = delivererService.create(dto);
        return ResponseEntity.created(URI.create("/api/deliverers/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public DelivererDto update(@PathVariable Long id, @Valid @RequestBody DelivererDto dto) {
        return delivererService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        delivererService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
