package net.thesphynx.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShopRequest(
        @NotBlank String name,
        String description,
        String logoUrl,
        @NotNull Long ownerId) {
}
