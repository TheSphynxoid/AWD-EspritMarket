package net.thesphynx.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import net.thesphynx.delivery.enums.DeliveryStatus;
import net.thesphynx.delivery.enums.DeliveryType;

import java.time.LocalDateTime;

public class DeliveryDto {

    private Long id;

    @NotBlank(message = "Le nom du destinataire est obligatoire")
    private String recipientName;

    @NotBlank(message = "L'adresse est obligatoire")
    private String address;

    @NotBlank(message = "La ville est obligatoire")
    private String city;

    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "^[0-9]{4,5}$", message = "Code postal invalide (4 ou 5 chiffres)")
    private String postalCode;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Numéro de téléphone invalide")
    private String phone;

    @NotNull(message = "Les frais de livraison sont obligatoires")
    @PositiveOrZero(message = "Les frais de livraison doivent être positifs ou nuls")
    private Double deliveryFee;

    @NotNull(message = "Le type de livraison est obligatoire")
    private DeliveryType deliveryType;

    /** En lecture seule : géré par le service (PENDING à la création, puis PATCH /status). */
    private DeliveryStatus status;

    /** En lecture seule : généré à la création. */
    private LocalDateTime createdAt;

    /** En lecture seule : affecté via PUT /{id}/deliverer/{delivererId}. */
    private Long delivererId;

    public DeliveryDto() {
    }

    public DeliveryDto(Long id, String recipientName, String address, String city, String postalCode,
                       String phone, Double deliveryFee, DeliveryType deliveryType, DeliveryStatus status,
                       LocalDateTime createdAt, Long delivererId) {
        this.id = id;
        this.recipientName = recipientName;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.phone = phone;
        this.deliveryFee = deliveryFee;
        this.deliveryType = deliveryType;
        this.status = status;
        this.createdAt = createdAt;
        this.delivererId = delivererId;
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

    public Long getDelivererId() { return delivererId; }
    public void setDelivererId(Long delivererId) { this.delivererId = delivererId; }
}
