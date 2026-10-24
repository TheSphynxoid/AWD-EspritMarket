package net.thesphynx.srv.controller;

import jakarta.validation.Valid;
import net.thesphynx.srv.entity.ServiceOffer;
import net.thesphynx.srv.service.ServiceOfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/srv")
public class ServiceOfferController {

    private final ServiceOfferService service;

    public ServiceOfferController(ServiceOfferService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServiceOffer> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOffer> get(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ServiceOffer> create(@Valid @RequestBody ServiceOffer offer) {
        return ResponseEntity.ok(service.create(offer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceOffer> update(@PathVariable Long id, @Valid @RequestBody ServiceOffer offer) {
        return service.update(id, offer)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
