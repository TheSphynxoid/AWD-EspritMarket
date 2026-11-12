package net.thesphynx.marketplace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ShopRequest(
        @NotBlank String name,
        String description,
        String logoUrl,
        @NotBlank String ownerId,
        @NotBlank @Email String ownerEmail) {
}