package net.thesphynx.event.controller;

import jakarta.validation.Valid;
import net.thesphynx.event.dto.EquipmentDto;
import net.thesphynx.event.service.EquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/mic1/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<EquipmentDto> findAll() {
        return equipmentService.findAll();
    }

    @GetMapping("/{id}")
    public EquipmentDto findById(@PathVariable Long id) {
        return equipmentService.findById(id);
    }

    @PostMapping
    public ResponseEntity<EquipmentDto> create(@Valid @RequestBody EquipmentDto dto) {
        EquipmentDto created = equipmentService.create(dto);
        return ResponseEntity.created(URI.create("/mic1/equipments/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public EquipmentDto update(@PathVariable Long id, @Valid @RequestBody EquipmentDto dto) {
        return equipmentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
