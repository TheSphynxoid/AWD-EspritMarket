package net.thesphynx.delivery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.thesphynx.delivery.enums.VehicleType;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le type du véhicule est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @NotBlank(message = "Le numéro d'immatriculation est obligatoire")
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    /** Disponible par défaut si non renseigné. */
    @Column(nullable = false)
    private Boolean available = true;

    public Vehicle() {
    }

    public Vehicle(VehicleType type, String registrationNumber, Boolean available) {
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
