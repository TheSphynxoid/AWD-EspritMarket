package net.thesphynx.event.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String location;

    @Column(name = "meeting_link")
    private String meetingLink;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String name;

    @Column(name = "nb_tickets")
    private Integer nbTickets;

    private Boolean online;

    private String status;

    /** Id de l'utilisateur (microservice User) : pas de relation JPA entre microservices. */
    @Column(name = "user_id")
    private Long userId;

    public Event() {
    }

    public Event(LocalDate date, String location, String meetingLink, String name, Integer nbTickets, Boolean online, String status, Long userId) {
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