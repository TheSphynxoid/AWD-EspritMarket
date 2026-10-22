package net.thesphynx.delivery.service;

import net.thesphynx.delivery.entity.Deliverer;
import net.thesphynx.delivery.entity.Delivery;
import net.thesphynx.delivery.entity.Vehicle;
import net.thesphynx.delivery.repository.DelivererRepository;
import net.thesphynx.delivery.repository.DeliveryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DelivererService {

    private final DelivererRepository delivererRepository;
    private final DeliveryRepository deliveryRepository;
    private final VehicleService vehicleService;

    public DelivererService(DelivererRepository delivererRepository, DeliveryRepository deliveryRepository,
                            VehicleService vehicleService) {
        this.delivererRepository = delivererRepository;
        this.deliveryRepository = deliveryRepository;
        this.vehicleService = vehicleService;
    }

    public List<Deliverer> findAll() {
        return delivererRepository.findAll();
    }

    public Deliverer findById(Long id) {
        return getDeliverer(id);
    }

    public Deliverer create(Deliverer data) {
        Deliverer deliverer = new Deliverer();
        deliverer.setFullName(data.getFullName());
        deliverer.setPhone(data.getPhone());
        deliverer.setAvailable(data.getAvailable() != null ? data.getAvailable() : true);
        deliverer.setVehicle(resolveVehicle(data.getVehicle(), null));
        return delivererRepository.save(deliverer);
    }

    public Deliverer update(Long id, Deliverer data) {
        Deliverer deliverer = getDeliverer(id);
        deliverer.setFullName(data.getFullName());
        deliverer.setPhone(data.getPhone());
        if (data.getAvailable() != null) {
            deliverer.setAvailable(data.getAvailable());
        }
        deliverer.setVehicle(resolveVehicle(data.getVehicle(), id));
        return delivererRepository.save(deliverer);
    }

    @Transactional
    public void delete(Long id) {
        Deliverer deliverer = getDeliverer(id);
        // Les livraisons restent, mais sans livreur affecté.
        for (Delivery delivery : deliveryRepository.findByDelivererId(id)) {
            delivery.setDeliverer(null);
        }
        delivererRepository.delete(deliverer);
    }

    public Deliverer getDeliverer(Long id) {
        return delivererRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Livreur introuvable : " + id));
    }

    /** Charge le véhicule demandé ({"id": ...}) et vérifie qu'il n'est pas déjà associé à un autre livreur. */
    private Vehicle resolveVehicle(Vehicle requested, Long delivererId) {
        if (requested == null || requested.getId() == null) {
            return null;
        }
        Vehicle vehicle = vehicleService.getVehicle(requested.getId());
        boolean usedByOther = delivererRepository.findByVehicleId(vehicle.getId()).stream()
                .anyMatch(d -> !d.getId().equals(delivererId));
        if (usedByOther) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Véhicule déjà associé à un autre livreur : " + vehicle.getId());
        }
        return vehicle;
    }
}
