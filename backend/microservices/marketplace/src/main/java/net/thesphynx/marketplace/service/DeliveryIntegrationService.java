package net.thesphynx.marketplace.service;

import net.thesphynx.marketplace.client.DeliveryFeignClient;
import net.thesphynx.marketplace.dto.DeliveryDto;
import net.thesphynx.marketplace.dto.DeliveryStatusDto;
import lombok.extern.slf4j.Slf4j;
import net.thesphynx.marketplace.enums.DeliveryStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DeliveryIntegrationService {

    private final DeliveryFeignClient deliveryFeignClient;

    public DeliveryIntegrationService(DeliveryFeignClient deliveryFeignClient) {
        this.deliveryFeignClient = deliveryFeignClient;
    }

    /** Appel bloquant via Feign : cree la livraison et attend la reponse de delivery. */
    public DeliveryDto createDeliverySync(DeliveryDto dto) {
        // Le frais de transport est fixe a 8 DT (non modifiable par l'utilisateur)
        dto.setDeliveryFee(8.0);
        log.info("[SYNC] Appel Feign vers Delivery pour creer une livraison : {}", dto.getRecipientName());
        DeliveryDto created = deliveryFeignClient.create(dto);
        log.info("[SYNC] Reponse recue de Delivery, id={}", created != null ? created.getId() : null);
        return created;
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