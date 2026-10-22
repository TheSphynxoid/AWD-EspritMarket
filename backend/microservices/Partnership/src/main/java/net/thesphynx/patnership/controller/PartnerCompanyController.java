package net.thesphynx.patnership.controller;

import jakarta.validation.Valid;
import net.thesphynx.patnership.dto.PartnerCompanyDto;
import net.thesphynx.patnership.service.PartnerCompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/partnership/companies")
public class PartnerCompanyController {

    private final PartnerCompanyService partnerCompanyService;

    public PartnerCompanyController(PartnerCompanyService partnerCompanyService) {
        this.partnerCompanyService = partnerCompanyService;
    }

    @GetMapping
    public List<PartnerCompanyDto> findAll() {
        return partnerCompanyService.findAll();
    }

    @GetMapping("/{id}")
    public PartnerCompanyDto findById(@PathVariable Long id) {
        return partnerCompanyService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PartnerCompanyDto> create(@Valid @RequestBody PartnerCompanyDto dto) {
        PartnerCompanyDto created = partnerCompanyService.create(dto);
        return ResponseEntity.created(URI.create("/api/partnership/companies/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public PartnerCompanyDto update(@PathVariable Long id, @Valid @RequestBody PartnerCompanyDto dto) {
        return partnerCompanyService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        partnerCompanyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
