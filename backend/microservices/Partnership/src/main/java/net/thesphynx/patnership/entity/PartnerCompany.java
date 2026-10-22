package net.thesphynx.patnership.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "partner_companies")
public class PartnerCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Column(nullable = false)
    private String name;

    private String sector;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "partnership_status")
    private String partnershipStatus;

    private String website;

    private String phone;

    public PartnerCompany() {
    }

    public PartnerCompany(String name, String sector, String contactEmail,
                          String partnershipStatus, String website, String phone) {
        this.name = name;
        this.sector = sector;
        this.contactEmail = contactEmail;
        this.partnershipStatus = partnershipStatus;
        this.website = website;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPartnershipStatus() {
        return partnershipStatus;
    }

    public void setPartnershipStatus(String partnershipStatus) {
        this.partnershipStatus = partnershipStatus;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
