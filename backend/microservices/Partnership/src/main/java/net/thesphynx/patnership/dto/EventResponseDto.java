package net.thesphynx.patnership.dto;

import java.time.LocalDate;

/**
 * DTO record that mirrors the Event service's response body.
 * Used by the FeignClient to deserialize GET /mic1/events/{id}.
 */
public record EventResponseDto(
        Long id,
        LocalDate date,
        String location,
        String meetingLink,
        String name,
        Integer nbTickets,
        Boolean online,
        String status,
        Long userId
) {
}

