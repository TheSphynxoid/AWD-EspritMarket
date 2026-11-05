package net.thesphynx.marketplace.service;

import net.thesphynx.marketplace.client.DeliveryFeignClient;
import net.thesphynx.marketplace.dto.DeliveryDto;
import net.thesphynx.marketplace.dto.DeliveryStatusDto;
import net.thesphynx.marketplace.enums.DeliveryStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryIntegrationService {

    private final DeliveryFeignClient deliveryFeignClient;

    public DeliveryIntegrationService(DeliveryFeignClient deliveryFeignClient) {
        this.deliveryFeignClient = deliveryFeignClient;
    }

    /** Appel bloquant via Feign : cree la livraison et attend la reponse de delivery. */
    public DeliveryDto createDeliverySync(DeliveryDto dto) {
        return deliveryFeignClient.create(dto);
    }

    public DeliveryDto getDeliverySync(Long id) {
        return deliveryFeignClient.findById(id);
    }

    public List<DeliveryDto> listDeliveriesSync() {
        return deliveryFeignClient.findAll();
    }

    public DeliveryDto updateDeliveryStatusSync(Long id, DeliveryStatus status) {
        return deliveryFeignClient.updateStatus(id, new DeliveryStatusDto(status));
    }
}