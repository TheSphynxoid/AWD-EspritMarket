package net.thesphynx.marketplace.dto;

import java.math.BigDecimal;
import java.util.List;

public record PanierResponse(Long panierId, Long userId, List<LignePanierResponse> lignes,
                           BigDecimal totalAmount) {
}
