package net.thesphynx.event.service;

import net.thesphynx.event.dto.EquipmentDto;
import net.thesphynx.event.entity.Equipment;
import net.thesphynx.event.repository.EquipmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EventService eventService;

    public EquipmentService(EquipmentRepository equipmentRepository, EventService eventService) {
        this.equipmentRepository = equipmentRepository;
        this.eventService = eventService;
    }

    public List<EquipmentDto> findAll() {
        return equipmentRepository.findAll().stream().map(this::toDto).toList();
    }

    public EquipmentDto findById(Long id) {
        return toDto(getEquipment(id));
    }

    public EquipmentDto create(EquipmentDto dto) {
        return toDto(equipmentRepository.save(toEntity(dto)));
    }

    public EquipmentDto update(Long id, EquipmentDto dto) {
        Equipment equipment = getEquipment(id);
        updateEntity(equipment, dto);
        return toDto(equipmentRepository.save(equipment));
    }

    public void delete(Long id) {
        equipmentRepository.delete(getEquipment(id));
    }

    private Equipment getEquipment(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Équipement introuvable : " + id));
    }

    private Equipment toEntity(EquipmentDto dto) {
        Equipment equipment = new Equipment();
        updateEntity(equipment, dto);
        return equipment;
    }

    private void updateEntity(Equipment equipment, EquipmentDto dto) {
        equipment.setImageUrl(dto.getImageUrl());
        equipment.setName(dto.getName());
        equipment.setQuantity(dto.getQuantity());
        equipment.setStatus(dto.getStatus());
        equipment.setType(dto.getType());
        equipment.setEvent(eventService.getEvent(dto.getEventId()));
    }

    private EquipmentDto toDto(Equipment equipment) {
        return new EquipmentDto(equipment.getId(), equipment.getImageUrl(), equipment.getName(),
                equipment.getQuantity(), equipment.getStatus(), equipment.getType(),
                equipment.getEvent().getId());
    }
}
