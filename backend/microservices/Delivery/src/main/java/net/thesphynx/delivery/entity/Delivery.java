package net.thesphynx.delivery.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import net.thesphynx.delivery.enums.DeliveryStatus;
import net.thesphynx.delivery.enums.DeliveryType;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du destinataire est obligatoire")
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @NotBlank(message = "L'adresse est obligatoire")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "La ville est obligatoire")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "^[0-9]{4,5}$", message = "Code postal invalide (4 ou 5 chiffres)")
    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Numéro de téléphone invalide")
    @Column(nullable = false)
    private String phone;

    @NotNull(message = "Les frais de livraison sont obligatoires")
    @PositiveOrZero(message = "Les frais de livraison doivent être positifs ou nuls")
    @Column(name = "delivery_fee", nullable = false)
    private Double deliveryFee;

    @NotNull(message = "Le type de livraison est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false)
    private DeliveryType deliveryType;

    /** Géré par le service (PENDING à la création, puis PATCH /status). */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Many-to-one optionnel : delivery.deliverer_id -> deliverer.id. */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne
    @JoinColumn(name = "deliverer_id")
    private Deliverer deliverer;

    public Delivery() {
    }

    public Delivery(String recipientName, String address, String city, String postalCode,
                    String phone, Double deliveryFee, DeliveryType deliveryType) {
        this.recipientName = recipientName;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.phone = phone;
        this.deliveryFee = deliveryFee;
        this.deliveryType = deliveryType;
    }

    @PrePersist
    void onCreate() {
        if (status == null) {
            status = DeliveryStatus.PENDING;
        }
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(Double deliveryFee) { this.deliveryFee = deliveryFee; }

    public DeliveryType getDeliveryType() { return deliveryType; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Deliverer getDeliverer() { return deliverer; }
    public void setDeliverer(Deliverer deliverer) { this.deliverer = deliverer; }
}
