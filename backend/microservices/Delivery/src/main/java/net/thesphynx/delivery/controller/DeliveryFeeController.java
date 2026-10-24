package net.thesphynx.delivery.controller;

import jakarta.validation.Valid;
import net.thesphynx.delivery.dto.DeliveryFeeDto;
import net.thesphynx.delivery.enums.DeliveryType;
import net.thesphynx.delivery.service.DeliveryFeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Sous /api/deliveries pour passer par la route gateway déjà existante. */
@RestController
@RequestMapping("/api/deliveries/fees")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    @GetMapping
    public List<DeliveryFeeDto> findAll() {
        return deliveryFeeService.findAll();
    }

    @PutMapping("/{type}")
    public DeliveryFeeDto update(@PathVariable DeliveryType type, @Valid @RequestBody DeliveryFeeDto dto) {
        return deliveryFeeService.update(type, dto.getAmount());
    }
}
