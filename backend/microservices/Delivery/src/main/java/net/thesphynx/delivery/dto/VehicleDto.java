package net.thesphynx.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.thesphynx.delivery.enums.VehicleType;

public class VehicleDto {

    private Long id;

    @NotNull(message = "Le type du véhicule est obligatoire")
    private VehicleType type;

    @NotBlank(message = "Le numéro d'immatriculation est obligatoire")
    private String registrationNumber;

    /** Disponible par défaut si non renseigné. */
    private Boolean available;

    public VehicleDto() {
    }

    public VehicleDto(Long id, VehicleType type, String registrationNumber, Boolean available) {
        this.id = id;
        this.type = type;
        this.registrationNumber = registrationNumber;
        this.available = available;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}
