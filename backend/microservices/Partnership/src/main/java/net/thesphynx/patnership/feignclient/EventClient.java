package net.thesphynx.patnership.feignclient;

import net.thesphynx.patnership.dto.EventResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client for the Event microservice.
 * <p>
 * {@code name = "event"} must match the Event service's
 * {@code spring.application.name=event} so that Eureka can resolve the host
 * automatically — no hardcoded URLs.
 */
@FeignClient(name = "event")
public interface EventClient {

    @GetMapping("/mic1/events/{id}")
    EventResponseDto findEventById(@PathVariable("id") Long id);

    @GetMapping("/mic1/events")
    List<EventResponseDto> findAllEvents();

    @GetMapping("/mic1/events/by-status/{status}")
    List<EventResponseDto> findEventsByStatus(@PathVariable("status") String status);
}

