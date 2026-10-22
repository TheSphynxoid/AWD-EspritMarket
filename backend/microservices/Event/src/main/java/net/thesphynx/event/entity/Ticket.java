package net.thesphynx.event.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_status")
    private String paymentStatus;

    private Double price;

    private String type;

    /** Many-to-one : ticket.event_id -> event.id. */
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    /** Id de l'utilisateur (microservice User). */
    @Column(name = "user_id")
    private Long userId;

    public Ticket() {
    }

    public Ticket(String paymentStatus, Double price, String type, Event event, Long userId) {
        this.paymentStatus = paymentStatus;
        this.price = price;
        this.type = type;
        this.event = event;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}