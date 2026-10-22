package net.thesphynx.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EquipmentDto {

    private Long id;
    private String imageUrl;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    private Integer quantity;
    private String status;
    private String type;

    @NotNull(message = "L'événement est obligatoire")
    private Long eventId;

    public EquipmentDto() {
    }

    public EquipmentDto(Long id, String imageUrl, String name, Integer quantity,
                        String status, String type, Long eventId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.quantity = quantity;
        this.status = status;
        this.type = type;
        this.eventId = eventId;
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

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
}
