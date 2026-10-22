package net.thesphynx.event.controller;

import jakarta.validation.Valid;
import net.thesphynx.event.dto.TicketDto;
import net.thesphynx.event.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/mic1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<TicketDto> findAll() {
        return ticketService.findAll();
    }

    @GetMapping("/{id}")
    public TicketDto findById(@PathVariable Long id) {
        return ticketService.findById(id);
    }

    @PostMapping
    public ResponseEntity<TicketDto> create(@Valid @RequestBody TicketDto dto) {
        TicketDto created = ticketService.create(dto);
        return ResponseEntity.created(URI.create("/mic1/tickets/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public TicketDto update(@PathVariable Long id, @Valid @RequestBody TicketDto dto) {
        return ticketService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
