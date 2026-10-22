package net.thesphynx.marketplace.controller;

import jakarta.validation.Valid;
import java.util.List;
import net.thesphynx.marketplace.dto.CategoryRequest;
import net.thesphynx.marketplace.dto.CategoryResponse;
import net.thesphynx.marketplace.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mic2/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return service.findAll();
    }
}