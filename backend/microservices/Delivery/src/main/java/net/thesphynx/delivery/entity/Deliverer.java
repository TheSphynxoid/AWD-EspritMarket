package net.thesphynx.delivery.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "deliverer")
public class Deliverer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    /** Disponible par défaut si non renseigné. */
    @Column(nullable = false)
    private Boolean available = true;

    /** One-to-one optionnel : deliverer.vehicle_id -> vehicle.id. */
    @OneToOne
    @JoinColumn(name = "vehicle_id", unique = true)
    private Vehicle vehicle;

    public Deliverer() {
    }

    public Deliverer(String fullName, String phone, Boolean available, Vehicle vehicle) {
        this.fullName = fullName;
        this.phone = phone;
        this.available = available;
        this.vehicle = vehicle;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
}
