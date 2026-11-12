package net.thesphynx.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thesphynx.marketplace.enums.DeliveryStatus;

/**
 * Miroir de delivery.dto.DeliveryStatusDto : {"status": "IN_PROGRESS"}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusDto {
    private DeliveryStatus status;
}