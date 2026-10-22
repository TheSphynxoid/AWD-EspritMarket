package net.thesphynx.event.controller;

import jakarta.validation.Valid;
import net.thesphynx.event.dto.EventDto;
import net.thesphynx.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/mic1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDto> findAll() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public EventDto findById(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @PostMapping
    public ResponseEntity<EventDto> create(@Valid @RequestBody EventDto dto) {
        EventDto created = eventService.create(dto);
        return ResponseEntity.created(URI.create("/mic1/events/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public EventDto update(@PathVariable Long id, @Valid @RequestBody EventDto dto) {
        return eventService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-status/{status}")
    public List<EventDto> findByStatus(@PathVariable("status") String status) {
        return eventService.findByStatus(status);
    }

    @GetMapping("/by-user/{userId}")
    public List<EventDto> findByUserId(@PathVariable("userId") Long userId) {
        return eventService.findByUserId(userId);
    }
}
