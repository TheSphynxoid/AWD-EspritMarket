package net.thesphynx.event.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String name;

    private Integer quantity;

    private String status;

    private String type;

    /** Many-to-one : equipment.event_id -> event.id. */
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public Equipment() {
    }

    public Equipment(String imageUrl, String name, Integer quantity, String status, String type, Event event) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.quantity = quantity;
        this.status = status;
        this.type = type;
        this.event = event;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}