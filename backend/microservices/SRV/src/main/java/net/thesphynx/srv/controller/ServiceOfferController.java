package net.thesphynx.srv.controller;

import jakarta.validation.Valid;
import net.thesphynx.srv.dto.ServiceRequest;
import net.thesphynx.srv.dto.ServiceResponse;
import net.thesphynx.srv.security.CurrentUser;
import net.thesphynx.srv.service.ServiceOfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/srv")
public class ServiceOfferController {

    private final ServiceOfferService service;

    public ServiceOfferController(ServiceOfferService service) {
        this.service = service;
    }

    /** Catalogue public : ?q=texte&category=...&minPrice=...&maxPrice=... */
    @GetMapping
    public List<ServiceResponse> list(@RequestParam(required = false) String q,
                                      @RequestParam(required = false) String category,
                                      @RequestParam(required = false) BigDecimal minPrice,
                                      @RequestParam(required = false) BigDecimal maxPrice) {
        return service.search(q, category, minPrice, maxPrice);
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return service.categories();
    }

    /** Services du prestataire connecté (y compris en pause). */
    @GetMapping("/mine")
    public List<ServiceResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return service.mine(CurrentUser.from(jwt));
    }

    @GetMapping("/{id}")
    public ServiceResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.get(id, jwt != null ? CurrentUser.from(jwt) : null);
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> create(@Valid @RequestBody ServiceRequest req, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req, CurrentUser.from(jwt)));
    }

    @PutMapping("/{id}")
    public ServiceResponse update(@PathVariable Long id, @Valid @RequestBody ServiceRequest req,
                                  @AuthenticationPrincipal Jwt jwt) {
        return service.update(id, req, CurrentUser.from(jwt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, CurrentUser.from(jwt));
        return ResponseEntity.noContent().build();
    }
}
