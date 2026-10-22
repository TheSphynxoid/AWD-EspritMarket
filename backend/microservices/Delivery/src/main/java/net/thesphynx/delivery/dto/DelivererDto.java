package net.thesphynx.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class DelivererDto {

    private Long id;

    @NotBlank(message = "Le nom complet est obligatoire")
    private String fullName;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Numéro de téléphone invalide")
    private String phone;

    /** Disponible par défaut si non renseigné. */
    private Boolean available;

    /** Optionnel : id du véhicule associé. */
    private Long vehicleId;

    public DelivererDto() {
    }

    public DelivererDto(Long id, String fullName, String phone, Boolean available, Long vehicleId) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.available = available;
        this.vehicleId = vehicleId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
}
