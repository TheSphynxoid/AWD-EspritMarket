package net.thesphynx.marketplace.dto;

import java.math.BigDecimal;
import java.util.List;

public record PanierResponse(Long panierId, String userId, List<LignePanierResponse> lignes,
                           BigDecimal totalAmount) {
}
