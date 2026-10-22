package net.thesphynx.patnership.controller;

import jakarta.validation.Valid;
import net.thesphynx.patnership.dto.ApplicationDto;
import net.thesphynx.patnership.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/partnership/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public List<ApplicationDto> findAll() {
        return applicationService.findAll();
    }

    @GetMapping("/{id}")
    public ApplicationDto findById(@PathVariable Long id) {
        return applicationService.findById(id);
    }

    @GetMapping("/job-offer/{jobOfferId}")
    public List<ApplicationDto> findByJobOfferId(@PathVariable Long jobOfferId) {
        return applicationService.findByJobOfferId(jobOfferId);
    }

    @GetMapping("/user/{userId}")
    public List<ApplicationDto> findByUserId(@PathVariable Long userId) {
        return applicationService.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<ApplicationDto> create(@Valid @RequestBody ApplicationDto dto) {
        ApplicationDto created = applicationService.create(dto);
        return ResponseEntity.created(URI.create("/api/partnership/applications/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ApplicationDto update(@PathVariable Long id, @Valid @RequestBody ApplicationDto dto) {
        return applicationService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
