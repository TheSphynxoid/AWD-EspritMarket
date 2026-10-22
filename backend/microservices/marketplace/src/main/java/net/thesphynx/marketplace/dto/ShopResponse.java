package net.thesphynx.marketplace.dto;

import java.time.LocalDateTime;
import net.thesphynx.marketplace.entity.ShopStatus;

public record ShopResponse(Long id, String name, String description, String logoUrl,
                           Long ownerId, ShopStatus status, LocalDateTime createdAt) {
}
