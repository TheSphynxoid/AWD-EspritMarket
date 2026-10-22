package net.thesphynx.delivery.service;

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

    public DeliveryService(DeliveryRepository deliveryRepository, DelivererRepository delivererRepository,
                           DelivererService delivererService) {
        this.deliveryRepository = deliveryRepository;
        this.delivererRepository = delivererRepository;
        this.delivererService = delivererService;
    }

    public List<Delivery> findAll() {
        return deliveryRepository.findAll();
    }

    public Delivery findById(Long id) {
        return getDelivery(id);
    }

    public Delivery create(Delivery data) {
        Delivery delivery = new Delivery();
        copyInfo(delivery, data);
        // status = PENDING et createdAt générés dans @PrePersist
        return deliveryRepository.save(delivery);
    }

    public Delivery update(Long id, Delivery data) {
        Delivery delivery = getDelivery(id);
        ensureNotClosed(delivery);
        copyInfo(delivery, data);
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery updateStatus(Long id, DeliveryStatus status) {
        Delivery delivery = getDelivery(id);
        ensureNotClosed(delivery);
        delivery.setStatus(status);
        // Livraison terminée ou annulée : le livreur redevient disponible.
        if ((status == DeliveryStatus.DELIVERED || status == DeliveryStatus.CANCELLED)
                && delivery.getDeliverer() != null) {
            release(delivery.getDeliverer());
        }
        return deliveryRepository.save(delivery);
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
    public Delivery assignDeliverer(Long id, Long delivererId) {
        Delivery delivery = getDelivery(id);
        ensureNotClosed(delivery);
        Deliverer deliverer = delivererService.getDeliverer(delivererId);

        if (delivery.getDeliverer() != null && delivery.getDeliverer().getId().equals(delivererId)) {
            return delivery;
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
        return deliveryRepository.save(delivery);
    }

    private Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Livraison introuvable : " + id));
    }

    private void copyInfo(Delivery delivery, Delivery data) {
        delivery.setRecipientName(data.getRecipientName());
        delivery.setAddress(data.getAddress());
        delivery.setCity(data.getCity());
        delivery.setPostalCode(data.getPostalCode());
        delivery.setPhone(data.getPhone());
        delivery.setDeliveryFee(data.getDeliveryFee());
        delivery.setDeliveryType(data.getDeliveryType());
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
