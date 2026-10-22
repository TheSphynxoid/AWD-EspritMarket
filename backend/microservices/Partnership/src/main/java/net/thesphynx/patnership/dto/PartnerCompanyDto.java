package net.thesphynx.patnership.dto;

import jakarta.validation.constraints.NotBlank;

public class PartnerCompanyDto {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    private String sector;
    private String contactEmail;
    private String partnershipStatus;
    private String website;
    private String phone;

    public PartnerCompanyDto() {
    }

    public PartnerCompanyDto(Long id, String name, String sector, String contactEmail,
                             String partnershipStatus, String website, String phone) {
        this.id = id;
        this.name = name;
        this.sector = sector;
        this.contactEmail = contactEmail;
        this.partnershipStatus = partnershipStatus;
        this.website = website;
        this.phone = phone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getPartnershipStatus() { return partnershipStatus; }
    public void setPartnershipStatus(String partnershipStatus) { this.partnershipStatus = partnershipStatus; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
