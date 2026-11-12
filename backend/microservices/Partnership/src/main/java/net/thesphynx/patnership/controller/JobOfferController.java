package net.thesphynx.patnership.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import net.thesphynx.patnership.dto.JobOfferDto;
import net.thesphynx.patnership.service.JobOfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

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

    // --- Feign exception handlers ---

    /**
     * If a Feign call to the Event service returns 404,
     * propagate it as 404 NOT_FOUND to the caller.
     */
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Map<String, String>> handleFeignNotFound(FeignException.NotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Remote resource not found",
                        "detail", ex.getMessage()
                ));
    }

    /**
     * If a Feign call fails for any other reason (service down, timeout, 5xx),
     * return 503 SERVICE_UNAVAILABLE.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeignException(FeignException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Event service unavailable",
                        "detail", ex.getMessage()
                ));
    }
}
