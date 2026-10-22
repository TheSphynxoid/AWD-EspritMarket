package net.thesphynx.marketplace.service;

import java.util.List;
import net.thesphynx.marketplace.dto.CategoryRequest;
import net.thesphynx.marketplace.dto.CategoryResponse;
import net.thesphynx.marketplace.entity.Category;
import net.thesphynx.marketplace.exception.DuplicateResourceException;
import net.thesphynx.marketplace.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository repository;
    public CategoryService(CategoryRepository repository) { this.repository = repository; }
    public CategoryResponse create(CategoryRequest request) {
        if (repository.existsByName(request.name())) throw new DuplicateResourceException("Une catégorie existe déjà avec ce nom");
        Category category = new Category(); category.setName(request.name()); return toResponse(repository.save(category));
    }
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }
    private CategoryResponse toResponse(Category category) { return new CategoryResponse(category.getId(), category.getName()); }
}
