package net.thesphynx.delivery.service;

import feign.FeignException;
import net.thesphynx.delivery.client.MarketplaceClient;
import net.thesphynx.delivery.dto.DeliveryDto;
import net.thesphynx.delivery.dto.ProductDto;
import net.thesphynx.delivery.entity.Deliverer;
import net.thesphynx.delivery.entity.Delivery;
import net.thesphynx.delivery.enums.DeliveryStatus;
import net.thesphynx.delivery.repository.DelivererRepository;
import net.thesphynx.delivery.repository.DeliveryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DelivererRepository delivererRepository;
    private final DelivererService delivererService;
    private final MarketplaceClient marketplaceClient;

    public DeliveryService(DeliveryRepository deliveryRepository, DelivererRepository delivererRepository,
                           DelivererService delivererService, MarketplaceClient marketplaceClient) {
        this.deliveryRepository = deliveryRepository;
        this.delivererRepository = delivererRepository;
        this.delivererService = delivererService;
        this.marketplaceClient = marketplaceClient;
    }

    public List<DeliveryDto> findAll() {
        return deliveryRepository.findAll().stream().map(this::toDto).toList();
    }

    public DeliveryDto findById(Long id) {
        return toDto(getDelivery(id));
    }

    public DeliveryDto create(DeliveryDto dto) {
        ensureProductAvailable(dto.getProductId());
        // status = PENDING et createdAt générés dans @PrePersist
        return toDto(deliveryRepository.save(toEntity(dto)));
    }

    public DeliveryDto update(Long id, DeliveryDto dto) {
        Delivery delivery = getDelivery(id);
        ensureNotClosed(delivery);
        if (!dto.getProductId().equals(delivery.getProductId())) {
            ensureProductAvailable(dto.getProductId());
        }
        updateEntity(delivery, dto);
        return toDto(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryDto updateStatus(Long id, DeliveryStatus status) {
        Delivery delivery = getDelivery(id);
        ensureNotClosed(delivery);
        delivery.setStatus(status);
        // Livraison terminée ou annulée : le livreur redevient disponible.
        if ((status == DeliveryStatus.DELIVERED || status == DeliveryStatus.CANCELLED)
                && delivery.getDeliverer() != null) {
            release(delivery.getDeliverer());
        }
        return toDto(deliveryRepository.save(delivery));
    }

    @Transactional
    public void delete(Long id) {
        Delivery delivery = getDelivery(id);
        if (delivery.getDeliverer() != null && !isClosed(delivery)) {
            release(delivery.getDeliverer());
        }
        deliveryRepository.delete(delivery);
    }

    @Transactional
    public DeliveryDto assignDeliverer(Long id, Long delivererId) {
        Delivery delivery = getDelivery(id);
        ensureNotClosed(delivery);
        Deliverer deliverer = delivererService.getDeliverer(delivererId);

        if (delivery.getDeliverer() != null && delivery.getDeliverer().getId().equals(delivererId)) {
            return toDto(delivery);
        }
        if (!Boolean.TRUE.equals(deliverer.getAvailable())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Livreur non disponible : " + delivererId);
        }
        // Réaffectation : l'ancien livreur est libéré.
        if (delivery.getDeliverer() != null) {
            release(delivery.getDeliverer());
        }
        deliverer.setAvailable(false);
        delivererRepository.save(deliverer);
        delivery.setDeliverer(deliverer);
        return toDto(deliveryRepository.save(delivery));
    }

    private Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Livraison introuvable : " + id));
    }

    /** Appel synchrone vers marketplace : le produit doit exister, être actif et en stock. */
    private void ensureProductAvailable(Long productId) {
        ProductDto product;
        try {
            product = marketplaceClient.getProduct(productId);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit introuvable : " + productId);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Microservice marketplace indisponible");
        }
        if (!Boolean.TRUE.equals(product.active()) || product.stock() == null || product.stock() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Produit indisponible : " + productId);
        }
    }

    private Delivery toEntity(DeliveryDto dto) {
        Delivery delivery = new Delivery();
        updateEntity(delivery, dto);
        return delivery;
    }

    /** status, createdAt et delivererId ne sont jamais pris depuis le DTO. */
    private void updateEntity(Delivery delivery, DeliveryDto dto) {
        delivery.setRecipientName(dto.getRecipientName());
        delivery.setAddress(dto.getAddress());
        delivery.setCity(dto.getCity());
        delivery.setPostalCode(dto.getPostalCode());
        delivery.setPhone(dto.getPhone());
        delivery.setDeliveryFee(dto.getDeliveryFee());
        delivery.setDeliveryType(dto.getDeliveryType());
        delivery.setProductId(dto.getProductId());
    }

    private DeliveryDto toDto(Delivery delivery) {
        return new DeliveryDto(delivery.getId(), delivery.getRecipientName(), delivery.getAddress(),
                delivery.getCity(), delivery.getPostalCode(), delivery.getPhone(), delivery.getDeliveryFee(),
                delivery.getDeliveryType(), delivery.getProductId(), delivery.getStatus(), delivery.getCreatedAt(),
                delivery.getDeliverer() != null ? delivery.getDeliverer().getId() : null);
    }

    private void release(Deliverer deliverer) {
        deliverer.setAvailable(true);
        delivererRepository.save(deliverer);
    }

    private boolean isClosed(Delivery delivery) {
        return delivery.getStatus() == DeliveryStatus.DELIVERED
                || delivery.getStatus() == DeliveryStatus.CANCELLED;
    }

    private void ensureNotClosed(Delivery delivery) {
        if (isClosed(delivery)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Livraison déjà clôturée (" + delivery.getStatus() + ") : " + delivery.getId());
        }
    }
}
