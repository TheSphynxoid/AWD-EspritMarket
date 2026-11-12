package net.thesphynx.srv.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import net.thesphynx.srv.entity.BookingStatus;

/** Changement de statut (accepter, refuser, terminer, annuler) avec un message optionnel. */
public record BookingStatusRequest(
        @NotNull(message = "Le statut est obligatoire")
        BookingStatus status,
        @Size(max = 1000, message = "Le message fait au plus 1000 caractères")
        String note) {
}
