package net.thesphynx.patnership.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class JobOfferDto {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;
    private String type;
    private String location;
    private String status;

    @NotNull(message = "L'entreprise partenaire est obligatoire")
    private Long companyId;

    // --- Event enrichment fields (populated via Feign call) ---
    private Long eventId;
    private String eventName;
    private LocalDate eventDate;
    private String eventLocation;

    public JobOfferDto() {
    }

    public JobOfferDto(Long id, String title, String description, String type,
                       String location, String status, Long companyId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.location = location;
        this.status = status;
        this.companyId = companyId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getEventLocation() { return eventLocation; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }
}
