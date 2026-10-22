package net.thesphynx.patnership.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "job_offers")
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private String type;

    private String location;

    private String status;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private PartnerCompany company;

    public JobOffer() {
    }

    public JobOffer(String title, String description, String type, String location,
                    String status, PartnerCompany company) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.location = location;
        this.status = status;
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PartnerCompany getCompany() {
        return company;
    }

    public void setCompany(PartnerCompany company) {
        this.company = company;
    }
}
