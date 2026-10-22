package net.thesphynx.event.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stall")
public class Stall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    private String name;

    private Integer number;

    private String status;

    /** Many-to-one : stall.event_id -> event.id. */
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    /** Id de l'utilisateur (microservice User). */
    @Column(name = "user_id")
    private Long userId;

    public Stall() {
    }

    public Stall(String location, String name, Integer number, String status, Event event, Long userId) {
        this.location = location;
        this.name = name;
        this.number = number;
        this.status = status;
        this.event = event;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}