package net.thesphynx.delivery.dto;

import jakarta.validation.constraints.NotNull;
import net.thesphynx.delivery.enums.DeliveryStatus;

/** Corps de PATCH /api/deliveries/{id}/status : {"status": "IN_PROGRESS"}. */
public class DeliveryStatusDto {

    @NotNull(message = "Le statut est obligatoire")
    private DeliveryStatus status;

    public DeliveryStatusDto() {
    }

    public DeliveryStatusDto(DeliveryStatus status) {
        this.status = status;
    }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }
}
