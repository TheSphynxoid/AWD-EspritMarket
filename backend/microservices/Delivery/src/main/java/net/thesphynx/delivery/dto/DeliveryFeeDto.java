package net.thesphynx.delivery.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import net.thesphynx.delivery.enums.DeliveryType;

/** Tarif d'un type de livraison. Corps de PUT /api/deliveries/fees/{type} : {"amount": 9.5}. */
public class DeliveryFeeDto {

    private DeliveryType deliveryType;

    @NotNull(message = "Le montant est obligatoire")
    @PositiveOrZero(message = "Le montant doit être positif ou nul")
    private Double amount;

    public DeliveryFeeDto() {
    }

    public DeliveryFeeDto(DeliveryType deliveryType, Double amount) {
        this.deliveryType = deliveryType;
        this.amount = amount;
    }

    public DeliveryType getDeliveryType() { return deliveryType; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
