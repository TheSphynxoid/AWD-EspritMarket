package net.thesphynx.event.service;

import net.thesphynx.event.dto.TicketDto;
import net.thesphynx.event.entity.Ticket;
import net.thesphynx.event.repository.TicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventService eventService;

    public TicketService(TicketRepository ticketRepository, EventService eventService) {
        this.ticketRepository = ticketRepository;
        this.eventService = eventService;
    }

    public List<TicketDto> findAll() {
        return ticketRepository.findAll().stream().map(this::toDto).toList();
    }

    public TicketDto findById(Long id) {
        return toDto(getTicket(id));
    }

    public TicketDto create(TicketDto dto) {
        return toDto(ticketRepository.save(toEntity(dto)));
    }

    public TicketDto update(Long id, TicketDto dto) {
        Ticket ticket = getTicket(id);
        updateEntity(ticket, dto);
        return toDto(ticketRepository.save(ticket));
    }

    public void delete(Long id) {
        ticketRepository.delete(getTicket(id));
    }

    private Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Billet introuvable : " + id));
    }

    private Ticket toEntity(TicketDto dto) {
        Ticket ticket = new Ticket();
        updateEntity(ticket, dto);
        return ticket;
    }

    private void updateEntity(Ticket ticket, TicketDto dto) {
        ticket.setPaymentStatus(dto.getPaymentStatus());
        ticket.setPrice(dto.getPrice());
        ticket.setType(dto.getType());
        ticket.setEvent(eventService.getEvent(dto.getEventId()));
        ticket.setUserId(dto.getUserId());
    }

    private TicketDto toDto(Ticket ticket) {
        return new TicketDto(ticket.getId(), ticket.getPaymentStatus(), ticket.getPrice(),
                ticket.getType(), ticket.getEvent().getId(),
                ticket.getUserId());
    }
}
