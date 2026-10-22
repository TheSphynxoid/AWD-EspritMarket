package net.thesphynx.patnership.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class InterviewDto {

    private Long id;
    private LocalDate date;
    private String location;
    private String type;
    private String status;
    private String notes;

    @NotNull(message = "La candidature est obligatoire")
    private Long applicationId;

    public InterviewDto() {
    }

    public InterviewDto(Long id, LocalDate date, String location, String type,
                        String status, String notes, Long applicationId) {
        this.id = id;
        this.date = date;
        this.location = location;
        this.type = type;
        this.status = status;
        this.notes = notes;
        this.applicationId = applicationId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
}
