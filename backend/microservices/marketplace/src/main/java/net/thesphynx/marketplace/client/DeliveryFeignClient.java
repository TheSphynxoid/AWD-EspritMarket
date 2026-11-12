package net.thesphynx.marketplace.client;

import net.thesphynx.marketplace.dto.DeliveryDto;
import net.thesphynx.marketplace.dto.DeliveryStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Client Feign (declaratif) vers le microservice Delivery.
 * Spring genere l'implementation a partir de cette interface.
 * Le nom "Delivery" est le service id Eureka (comme pour RestTemplate load-balanced).
 */
@FeignClient(name = "Delivery")
public interface DeliveryFeignClient {

    @PostMapping("/api/deliveries")
    DeliveryDto create(@RequestBody DeliveryDto dto);

    @GetMapping("/api/deliveries/{id}")
    DeliveryDto findById(@PathVariable("id") Long id);

    @GetMapping("/api/deliveries")
    List<DeliveryDto> findAll();

    @PatchMapping("/api/deliveries/{id}/status")
    DeliveryDto updateStatus(@PathVariable("id") Long id, @RequestBody DeliveryStatusDto dto);
}