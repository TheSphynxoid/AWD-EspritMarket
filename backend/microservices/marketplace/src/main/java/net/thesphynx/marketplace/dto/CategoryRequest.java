package net.thesphynx.marketplace.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(@NotBlank String name) {
}
