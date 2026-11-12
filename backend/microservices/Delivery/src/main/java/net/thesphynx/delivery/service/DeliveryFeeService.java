package net.thesphynx.delivery.service;

import net.thesphynx.delivery.dto.DeliveryFeeDto;
import net.thesphynx.delivery.entity.DeliveryFee;
import net.thesphynx.delivery.enums.DeliveryType;
import net.thesphynx.delivery.repository.DeliveryFeeRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Tarifs de livraison fixés par l'admin ; appliqués automatiquement à la création d'une livraison. */
@Service
public class DeliveryFeeService {

    /** Tarifs utilisés tant que l'admin n'a rien enregistré. */
    private static final Map<DeliveryType, Double> DEFAULT_FEES = Map.of(
            DeliveryType.STANDARD, 7.0,
            DeliveryType.EXPRESS, 12.0,
            DeliveryType.SAME_DAY, 15.0);

    private final DeliveryFeeRepository deliveryFeeRepository;

    public DeliveryFeeService(DeliveryFeeRepository deliveryFeeRepository) {
        this.deliveryFeeRepository = deliveryFeeRepository;
    }

    public List<DeliveryFeeDto> findAll() {
        return Arrays.stream(DeliveryType.values())
                .map(type -> new DeliveryFeeDto(type, getAmount(type)))
                .toList();
    }

    public Double getAmount(DeliveryType type) {
        return deliveryFeeRepository.findById(type)
                .map(DeliveryFee::getAmount)
                .orElse(DEFAULT_FEES.get(type));
    }

    public DeliveryFeeDto update(DeliveryType type, Double amount) {
        DeliveryFee fee = deliveryFeeRepository.save(new DeliveryFee(type, amount));
        return new DeliveryFeeDto(fee.getDeliveryType(), fee.getAmount());
    }
}
