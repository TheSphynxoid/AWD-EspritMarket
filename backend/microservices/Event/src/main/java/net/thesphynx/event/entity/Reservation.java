package net.thesphynx.event.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String name;

    /** Many-to-one : reservation.event_id -> event.id. */
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public Reservation() {
    }

    public Reservation(LocalDate date, String name, Event event) {
        this.date = date;
        this.name = name;
        this.event = event;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}