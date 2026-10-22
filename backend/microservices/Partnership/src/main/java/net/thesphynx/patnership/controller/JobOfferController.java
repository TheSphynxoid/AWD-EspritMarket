package net.thesphynx.patnership.controller;

import jakarta.validation.Valid;
import net.thesphynx.patnership.dto.JobOfferDto;
import net.thesphynx.patnership.service.JobOfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/partnership/job-offers")
public class JobOfferController {

    private final JobOfferService jobOfferService;

    public JobOfferController(JobOfferService jobOfferService) {
        this.jobOfferService = jobOfferService;
    }

    @GetMapping
    public List<JobOfferDto> findAll() {
        return jobOfferService.findAll();
    }

    @GetMapping("/{id}")
    public JobOfferDto findById(@PathVariable Long id) {
        return jobOfferService.findById(id);
    }

    @GetMapping("/company/{companyId}")
    public List<JobOfferDto> findByCompanyId(@PathVariable Long companyId) {
        return jobOfferService.findByCompanyId(companyId);
    }

    @PostMapping
    public ResponseEntity<JobOfferDto> create(@Valid @RequestBody JobOfferDto dto) {
        JobOfferDto created = jobOfferService.create(dto);
        return ResponseEntity.created(URI.create("/api/partnership/job-offers/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public JobOfferDto update(@PathVariable Long id, @Valid @RequestBody JobOfferDto dto) {
        return jobOfferService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobOfferService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
