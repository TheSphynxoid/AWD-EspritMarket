package net.thesphynx.event.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class EventDto {

    private Long id;
    private LocalDate date;
    private String location;
    private String meetingLink;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    private Integer nbTickets;
    private Boolean online;
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
