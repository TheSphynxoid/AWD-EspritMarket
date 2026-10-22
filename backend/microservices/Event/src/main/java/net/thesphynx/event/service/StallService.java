package net.thesphynx.event.service;

import net.thesphynx.event.dto.StallDto;
import net.thesphynx.event.entity.Stall;
import net.thesphynx.event.repository.StallRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StallService {

    private final StallRepository stallRepository;
    private final EventService eventService;

    public StallService(StallRepository stallRepository, EventService eventService) {
        this.stallRepository = stallRepository;
        this.eventService = eventService;
    }

    public List<StallDto> findAll() {
        return stallRepository.findAll().stream().map(this::toDto).toList();
    }

    public StallDto findById(Long id) {
        return toDto(getStall(id));
    }

    public StallDto create(StallDto dto) {
        return toDto(stallRepository.save(toEntity(dto)));
    }

    public StallDto update(Long id, StallDto dto) {
        Stall stall = getStall(id);
        updateEntity(stall, dto);
        return toDto(stallRepository.save(stall));
    }

    public void delete(Long id) {
        stallRepository.delete(getStall(id));
    }

    private Stall getStall(Long id) {
        return stallRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Stand introuvable : " + id));
    }

    private Stall toEntity(StallDto dto) {
        Stall stall = new Stall();
        updateEntity(stall, dto);
        return stall;
    }

    private void updateEntity(Stall stall, StallDto dto) {
        stall.setLocation(dto.getLocation());
        stall.setName(dto.getName());
        stall.setNumber(dto.getNumber());
        stall.setStatus(dto.getStatus());
        stall.setEvent(eventService.getEvent(dto.getEventId()));
        stall.setUserId(dto.getUserId());
    }

    private StallDto toDto(Stall stall) {
        return new StallDto(stall.getId(), stall.getLocation(), stall.getName(),
                stall.getNumber(), stall.getStatus(), stall.getEvent().getId(),
                stall.getUserId());
    }
}
