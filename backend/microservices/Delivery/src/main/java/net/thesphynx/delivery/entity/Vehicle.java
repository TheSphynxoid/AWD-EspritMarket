package net.thesphynx.delivery.entity;

import jakarta.persistence.*;
import net.thesphynx.delivery.enums.VehicleType;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

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
