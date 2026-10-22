package net.thesphynx.event.controller;

import jakarta.validation.Valid;
import net.thesphynx.event.dto.ReservationDto;
import net.thesphynx.event.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/mic1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationDto> findAll() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    public ReservationDto findById(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ReservationDto> create(@Valid @RequestBody ReservationDto dto) {
        ReservationDto created = reservationService.create(dto);
        return ResponseEntity.created(URI.create("/mic1/reservations/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ReservationDto update(@PathVariable Long id, @Valid @RequestBody ReservationDto dto) {
        return reservationService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
