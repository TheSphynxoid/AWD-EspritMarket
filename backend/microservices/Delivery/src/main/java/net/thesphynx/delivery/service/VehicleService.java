package net.thesphynx.delivery.service;

import net.thesphynx.delivery.entity.Deliverer;
import net.thesphynx.delivery.entity.Vehicle;
import net.thesphynx.delivery.repository.DelivererRepository;
import net.thesphynx.delivery.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DelivererRepository delivererRepository;

    public VehicleService(VehicleRepository vehicleRepository, DelivererRepository delivererRepository) {
        this.vehicleRepository = vehicleRepository;
        this.delivererRepository = delivererRepository;
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle findById(Long id) {
        return getVehicle(id);
    }

    public Vehicle create(Vehicle vehicle) {
        if (vehicleRepository.existsByRegistrationNumber(vehicle.getRegistrationNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Immatriculation déjà utilisée : " + vehicle.getRegistrationNumber());
        }
        vehicle.setId(null);
        if (vehicle.getAvailable() == null) {
            vehicle.setAvailable(true);
        }
        return vehicleRepository.save(vehicle);
    }

    public Vehicle update(Long id, Vehicle data) {
        Vehicle vehicle = getVehicle(id);
        if (vehicleRepository.existsByRegistrationNumberAndIdNot(data.getRegistrationNumber(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Immatriculation déjà utilisée : " + data.getRegistrationNumber());
        }
        vehicle.setType(data.getType());
        vehicle.setRegistrationNumber(data.getRegistrationNumber());
        if (data.getAvailable() != null) {
            vehicle.setAvailable(data.getAvailable());
        }
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = getVehicle(id);
        // Détache le véhicule des livreurs avant suppression (clé étrangère).
        for (Deliverer deliverer : delivererRepository.findByVehicleId(id)) {
            deliverer.setVehicle(null);
        }
        vehicleRepository.delete(vehicle);
    }

    public Vehicle getVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Véhicule introuvable : " + id));
    }
}
