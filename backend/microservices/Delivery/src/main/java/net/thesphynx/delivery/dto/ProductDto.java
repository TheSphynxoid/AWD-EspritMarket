package net.thesphynx.delivery.dto;

import java.math.BigDecimal;

/** Sous-ensemble du ProductResponse renvoyé par le microservice marketplace. */
public record ProductDto(Long id, String name, BigDecimal price, Integer stock,
                         Boolean active, Long shopId, String shopName) {
}
