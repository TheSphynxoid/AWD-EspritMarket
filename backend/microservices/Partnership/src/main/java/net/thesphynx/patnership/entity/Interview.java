package net.thesphynx.patnership.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "interview")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String location;

    private String type;

    private String status;

    @Column(columnDefinition = "text")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    public Interview() {
    }

    public Interview(LocalDate date, String location, String type, String status, String notes, Application application) {
        this.date = date;
        this.location = location;
        this.type = type;
        this.status = status;
        this.notes = notes;
        this.application = application;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
