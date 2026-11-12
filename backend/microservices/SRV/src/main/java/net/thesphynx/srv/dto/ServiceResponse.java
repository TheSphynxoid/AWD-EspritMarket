package net.thesphynx.srv.dto;

import net.thesphynx.srv.entity.ServiceMode;
import net.thesphynx.srv.entity.ServiceOffer;
import net.thesphynx.srv.entity.ServiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceResponse(
        Long id, String name, String description, String category, BigDecimal price,
        Integer durationMinutes, ServiceMode mode, String location, String imageUrl,
        ServiceStatus status, String providerId, String providerName,
        long bookingCount, LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static ServiceResponse of(ServiceOffer s, long bookingCount) {
        return new ServiceResponse(s.getId(), s.getName(), s.getDescription(), s.getCategory(), s.getPrice(),
                s.getDurationMinutes(), s.getMode(), s.getLocation(), s.getImageUrl(), s.getStatus(),
                s.getProviderId(), s.getProviderName(), bookingCount, s.getCreatedAt(), s.getUpdatedAt());
    }
}
