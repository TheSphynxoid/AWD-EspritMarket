package net.thesphynx.marketplace.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequestDto(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 1000) String description,
        @NotNull @DecimalMin(value = "0.0") BigDecimal price,
        @NotNull @Min(0) Integer stock,
        @Size(max = 100) String category,
        @Size(max = 100) String color) {
}
