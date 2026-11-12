package net.thesphynx.delivery.entity;

import jakarta.persistence.*;
import net.thesphynx.delivery.enums.DeliveryType;

/** Tarif de livraison fixé par l'administrateur, un par type de livraison. */
@Entity
@Table(name = "delivery_fee")
public class DeliveryFee {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    private DeliveryType deliveryType;

    @Column(nullable = false)
    private Double amount;

    public DeliveryFee() {
    }

    public DeliveryFee(DeliveryType deliveryType, Double amount) {
        this.deliveryType = deliveryType;
        this.amount = amount;
    }

    public DeliveryType getDeliveryType() { return deliveryType; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
