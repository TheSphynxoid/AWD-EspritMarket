package net.thesphynx.event.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class EventDto {

    private Long id;

    @NotNull(message = "La date est obligatoire")
    @FutureOrPresent(message = "La date doit être aujourd'hui ou dans le futur")
    private LocalDate date;

    @NotBlank(message = "Le lieu est obligatoire")
    @Pattern(regexp = "^(Bloc )?[ABCDEGHM]$", message = "Le lieu doit être un bloc parmi A, B, C, D, E, G, H, M")
    private String location;

    private String meetingLink;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 5, message = "Le nom doit contenir au moins 5 lettres")
    private String name;

    @NotNull(message = "Le nombre de tickets est obligatoire")
    @Min(value = 0, message = "Le nombre de tickets ne peut pas être négatif")
    private Integer nbTickets;

    private Boolean online;

    @Pattern(regexp = "PENDING|OPEN|PLANNED", message = "Le statut doit être PENDING, OPEN ou PLANNED")
    private String status;

    private Long userId;

    public EventDto() {
    }

    public EventDto(Long id, LocalDate date, String location, String meetingLink, String name,
                    Integer nbTickets, Boolean online, String status, Long userId) {
        this.id = id;
        this.date = date;
        this.location = location;
        this.meetingLink = meetingLink;
        this.name = name;
        this.nbTickets = nbTickets;
        this.online = online;
        this.status = status;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getNbTickets() { return nbTickets; }
    public void setNbTickets(Integer nbTickets) { this.nbTickets = nbTickets; }

    public Boolean getOnline() { return online; }
    public void setOnline(Boolean online) { this.online = online; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
