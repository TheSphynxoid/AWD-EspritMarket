package net.thesphynx.marketplace.dto;

import java.math.BigDecimal;

public record LignePanierResponse(Long itemId, Long productId, String productName,
                               BigDecimal unitPrice, Integer quantity, BigDecimal subtotal) {
}
