package net.thesphynx.delivery.service;

import net.thesphynx.delivery.dto.VehicleDto;
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

    public List<VehicleDto> findAll() {
        return vehicleRepository.findAll().stream().map(this::toDto).toList();
    }

    public VehicleDto findById(Long id) {
        return toDto(getVehicle(id));
    }

    public VehicleDto create(VehicleDto dto) {
        if (vehicleRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Immatriculation déjà utilisée : " + dto.getRegistrationNumber());
        }
        return toDto(vehicleRepository.save(toEntity(dto)));
    }

    public VehicleDto update(Long id, VehicleDto dto) {
        Vehicle vehicle = getVehicle(id);
        if (vehicleRepository.existsByRegistrationNumberAndIdNot(dto.getRegistrationNumber(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Immatriculation déjà utilisée : " + dto.getRegistrationNumber());
        }
        updateEntity(vehicle, dto);
        return toDto(vehicleRepository.save(vehicle));
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

    private Vehicle toEntity(VehicleDto dto) {
        Vehicle vehicle = new Vehicle();
        updateEntity(vehicle, dto);
        return vehicle;
    }

    private void updateEntity(Vehicle vehicle, VehicleDto dto) {
        vehicle.setType(dto.getType());
        vehicle.setRegistrationNumber(dto.getRegistrationNumber());
        if (dto.getAvailable() != null) {
            vehicle.setAvailable(dto.getAvailable());
        }
    }

    private VehicleDto toDto(Vehicle vehicle) {
        return new VehicleDto(vehicle.getId(), vehicle.getType(), vehicle.getRegistrationNumber(),
                vehicle.getAvailable());
    }
}
