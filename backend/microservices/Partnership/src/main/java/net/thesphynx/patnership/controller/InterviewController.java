package net.thesphynx.patnership.controller;

import jakarta.validation.Valid;
import net.thesphynx.patnership.dto.InterviewDto;
import net.thesphynx.patnership.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/partnership/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping
    public List<InterviewDto> findAll() {
        return interviewService.findAll();
    }

    @GetMapping("/{id}")
    public InterviewDto findById(@PathVariable Long id) {
        return interviewService.findById(id);
    }

    @GetMapping("/application/{applicationId}")
    public List<InterviewDto> findByApplicationId(@PathVariable Long applicationId) {
        return interviewService.findByApplicationId(applicationId);
    }

    @PostMapping
    public ResponseEntity<InterviewDto> create(@Valid @RequestBody InterviewDto dto) {
        InterviewDto created = interviewService.create(dto);
        return ResponseEntity.created(URI.create("/api/partnership/interviews/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public InterviewDto update(@PathVariable Long id, @Valid @RequestBody InterviewDto dto) {
        return interviewService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        interviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
