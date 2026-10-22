package net.thesphynx.event.dto;

import jakarta.validation.constraints.NotNull;

public class TicketDto {

    private Long id;
    private String paymentStatus;
    private Double price;
    private String type;

    @NotNull(message = "L'événement est obligatoire")
    private Long eventId;

    private Long userId;

    public TicketDto() {
    }

    public TicketDto(Long id, String paymentStatus, Double price, String type,
                     Long eventId, Long userId) {
        this.id = id;
        this.paymentStatus = paymentStatus;
        this.price = price;
        this.type = type;
        this.eventId = eventId;
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

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
