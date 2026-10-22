package net.thesphynx.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(Long id, String name, String description, BigDecimal price,
                              Integer stock, String imageUrl, Boolean active,
                              LocalDateTime createdAt, LocalDateTime updatedAt,
                              Long shopId, String shopName, Long categoryId, String categoryName) {
}
