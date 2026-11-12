package net.thesphynx.srv.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** Demande de réservation. Le client vient du JWT. */
public record BookingRequest(
        @NotNull(message = "Le service est obligatoire")
        Long serviceId,
        @NotNull(message = "La date est obligatoire")
        @Future(message = "La date doit être dans le futur")
        LocalDateTime scheduledAt,
        @Size(max = 1000, message = "Le message fait au plus 1000 caractères")
        String note) {
}
