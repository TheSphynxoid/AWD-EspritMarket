package net.thesphynx.event.controller;

import jakarta.validation.Valid;
import net.thesphynx.event.dto.StallDto;
import net.thesphynx.event.service.StallService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/mic1/stalls")
public class StallController {

    private final StallService stallService;

    public StallController(StallService stallService) {
        this.stallService = stallService;
    }

    @GetMapping
    public List<StallDto> findAll() {
        return stallService.findAll();
    }

    @GetMapping("/{id}")
    public StallDto findById(@PathVariable Long id) {
        return stallService.findById(id);
    }

    @PostMapping
    public ResponseEntity<StallDto> create(@Valid @RequestBody StallDto dto) {
        StallDto created = stallService.create(dto);
        return ResponseEntity.created(URI.create("/mic1/stalls/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public StallDto update(@PathVariable Long id, @Valid @RequestBody StallDto dto) {
        return stallService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stallService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
