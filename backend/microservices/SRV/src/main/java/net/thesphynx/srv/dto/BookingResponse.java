package net.thesphynx.srv.dto;

import net.thesphynx.srv.entity.Booking;
import net.thesphynx.srv.entity.BookingStatus;
import net.thesphynx.srv.entity.ServiceOffer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id, Long serviceId, String serviceName, BigDecimal servicePrice, String serviceImageUrl,
        String providerId, String providerName, String bookerId, String bookerName,
        LocalDateTime scheduledAt, String note, String providerNote, BookingStatus status,
        LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static BookingResponse of(Booking b) {
        ServiceOffer s = b.getService();
        return new BookingResponse(b.getId(),
                s != null ? s.getId() : null, s != null ? s.getName() : null,
                s != null ? s.getPrice() : null, s != null ? s.getImageUrl() : null,
                b.getProviderId(), s != null ? s.getProviderName() : null,
                b.getBookerId(), b.getBookerName(), b.getScheduledAt(), b.getNote(), b.getProviderNote(),
                b.getStatus(), b.getCreatedAt(), b.getUpdatedAt());
    }
}
