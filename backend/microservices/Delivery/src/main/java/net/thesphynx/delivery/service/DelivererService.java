package net.thesphynx.delivery.service;

import net.thesphynx.delivery.dto.DelivererDto;
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

    public List<DelivererDto> findAll() {
        return delivererRepository.findAll().stream().map(this::toDto).toList();
    }

    public DelivererDto findById(Long id) {
        return toDto(getDeliverer(id));
    }

    public DelivererDto create(DelivererDto dto) {
        Deliverer deliverer = new Deliverer();
        updateEntity(deliverer, dto);
        return toDto(delivererRepository.save(deliverer));
    }

    public DelivererDto update(Long id, DelivererDto dto) {
        Deliverer deliverer = getDeliverer(id);
        updateEntity(deliverer, dto);
        return toDto(delivererRepository.save(deliverer));
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

    private void updateEntity(Deliverer deliverer, DelivererDto dto) {
        deliverer.setFullName(dto.getFullName());
        deliverer.setPhone(dto.getPhone());
        if (dto.getAvailable() != null) {
            deliverer.setAvailable(dto.getAvailable());
        }
        deliverer.setVehicle(resolveVehicle(dto.getVehicleId(), deliverer.getId()));
    }

    /** Charge le véhicule demandé et vérifie qu'il n'est pas déjà associé à un autre livreur. */
    private Vehicle resolveVehicle(Long vehicleId, Long delivererId) {
        if (vehicleId == null) {
            return null;
        }
        Vehicle vehicle = vehicleService.getVehicle(vehicleId);
        boolean usedByOther = delivererRepository.findByVehicleId(vehicleId).stream()
                .anyMatch(d -> !d.getId().equals(delivererId));
        if (usedByOther) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Véhicule déjà associé à un autre livreur : " + vehicleId);
        }
        return vehicle;
    }

    private DelivererDto toDto(Deliverer deliverer) {
        return new DelivererDto(deliverer.getId(), deliverer.getFullName(), deliverer.getPhone(),
                deliverer.getAvailable(),
                deliverer.getVehicle() != null ? deliverer.getVehicle().getId() : null);
    }
}
