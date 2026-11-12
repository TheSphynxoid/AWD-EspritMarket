package net.thesphynx.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thesphynx.marketplace.enums.DeliveryStatus;
import net.thesphynx.marketplace.enums.DeliveryType;

import java.time.LocalDateTime;

/**
 * Copie locale (miroir) du DeliveryDto du microservice Delivery.
 * Permet a marketplace de communiquer sans dependre du module delivery.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    private Long id;

    private String recipientName;
    private String address;
    private String city;
    private String postalCode;
    private String phone;
    private Double deliveryFee;
    private DeliveryType deliveryType;
    /** Produit marketplace livré (obligatoire côté delivery, qui vérifie sa disponibilité). */
    private Long productId;

    /** En lecture seule cote delivery (PENDING a la creation). */
    private DeliveryStatus status;

    /** En lecture seule. */
    private LocalDateTime createdAt;

    /** En lecture seule. */
    private Long delivererId;
}