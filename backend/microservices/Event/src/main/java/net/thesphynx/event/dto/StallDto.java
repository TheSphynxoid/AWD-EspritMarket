package net.thesphynx.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StallDto {

    private Long id;
    private String location;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    private Integer number;
    private String status;

    @NotNull(message = "L'événement est obligatoire")
    private Long eventId;

    private Long userId;

    public StallDto() {
    }

    public StallDto(Long id, String location, String name, Integer number,
                    String status, Long eventId, Long userId) {
        this.id = id;
        this.location = location;
        this.name = name;
        this.number = number;
        this.status = status;
        this.eventId = eventId;
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

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
