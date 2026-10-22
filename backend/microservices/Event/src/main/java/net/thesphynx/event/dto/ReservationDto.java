package net.thesphynx.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ReservationDto {

    private Long id;
    private LocalDate date;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotNull(message = "L'événement est obligatoire")
    private Long eventId;

    public ReservationDto() {
    }

    public ReservationDto(Long id, LocalDate date, String name, Long eventId) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.eventId = eventId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
}
