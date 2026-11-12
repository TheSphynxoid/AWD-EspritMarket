package net.thesphynx.srv.dto;

import jakarta.validation.constraints.*;
import net.thesphynx.srv.entity.ServiceMode;
import net.thesphynx.srv.entity.ServiceStatus;

import java.math.BigDecimal;

/** Création / modification d'un service. Le prestataire vient du JWT, jamais du corps. */
public record ServiceRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 150, message = "Le nom fait au plus 150 caractères")
        String name,
        @Size(max = 2000, message = "La description fait au plus 2000 caractères")
        String description,
        @Size(max = 80, message = "La catégorie fait au plus 80 caractères")
        String category,
        @NotNull(message = "Le prix est obligatoire")
        @DecimalMin(value = "0.0", message = "Le prix doit être positif ou nul")
        @Digits(integer = 8, fraction = 2, message = "Prix invalide")
        BigDecimal price,
        @Min(value = 5, message = "Durée minimale : 5 minutes")
        @Max(value = 10080, message = "Durée maximale : 7 jours")
        Integer durationMinutes,
        ServiceMode mode,
        @Size(max = 150, message = "Le lieu fait au plus 150 caractères")
        String location,
        @Size(max = 1000, message = "L'URL de l'image fait au plus 1000 caractères")
        @Pattern(regexp = "^$|^https?://.+", message = "L'image doit être une URL http(s)")
        String imageUrl,
        /** Optionnel en création (ACTIVE par défaut). */
        ServiceStatus status) {
}
