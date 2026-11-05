package net.thesphynx.marketplace.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ModifierLignePanierRequest(
        @NotNull(message = "quantity est obligatoire")
        @Min(value = 1, message = "quantity doit être supérieure ou égale à 1") Integer quantity) {
}
