package net.thesphynx.event.service;

import net.thesphynx.event.config.RabbitConfig;
import net.thesphynx.event.dto.EventDto;
import net.thesphynx.event.entity.Event;
import net.thesphynx.event.repository.EventRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RabbitTemplate rabbitTemplate;

    public EventService(EventRepository eventRepository,
                        RabbitTemplate rabbitTemplate) {
        this.eventRepository = eventRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<EventDto> findAll() {
        return eventRepository.findAll().stream().map(this::toDto).toList();
    }

    public EventDto findById(Long id) {
        return toDto(getEvent(id));
    }

    public EventDto create(EventDto dto) {
        Event saved = eventRepository.save(toEntity(dto));
        EventDto created = toDto(saved);

        String recipient = "bahajouila7@gmail.com";
        Map<String, Object> message = Map.of(
                "recipient", recipient,
                "message", "Un nouvel événement '" + created.getName() + "' a été créé.",
                "type", "event"
        );
        rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATION_QUEUE, message);

        return created;
    }

    public EventDto update(Long id, EventDto dto) {
        Event event = getEvent(id);
        updateEntity(event, dto);
        return toDto(eventRepository.save(event));
    }

    public void delete(Long id) {
        Event event = getEvent(id);
        eventRepository.delete(event);
    }

    public List<EventDto> findByStatus(String status) {
        return eventRepository.findByStatus(status).stream().map(this::toDto).toList();
    }

    public List<EventDto> findByUserId(Long userId) {
        return eventRepository.findByUserId(userId).stream().map(this::toDto).toList();
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Événement introuvable : " + id));
    }

    private Event toEntity(EventDto dto) {
        Event event = new Event();
        updateEntity(event, dto);
        return event;
    }

    private void updateEntity(Event event, EventDto dto) {
        event.setDate(dto.getDate());
        event.setLocation(dto.getLocation());
        event.setMeetingLink(dto.getMeetingLink());
        event.setName(dto.getName());
        event.setNbTickets(dto.getNbTickets());
        event.setOnline(dto.getOnline());
        event.setStatus(dto.getStatus());
        event.setUserId(dto.getUserId());
    }

    private EventDto toDto(Event event) {
        return new EventDto(event.getId(), event.getDate(), event.getLocation(),
                event.getMeetingLink(), event.getName(), event.getNbTickets(),
                event.getOnline(), event.getStatus(), event.getUserId());
    }
}