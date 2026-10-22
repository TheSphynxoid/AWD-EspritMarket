package net.thesphynx.marketplace.service;

import java.util.List;
import java.util.stream.Collectors;
import net.thesphynx.marketplace.dto.ProductRequestDto;
import net.thesphynx.marketplace.dto.ProductResponseDto;
import net.thesphynx.marketplace.entity.Product;
import net.thesphynx.marketplace.exception.ProductNotFoundException;
import net.thesphynx.marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto create(ProductRequestDto request) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.category(),
                request.color());
        return toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> findAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findById(Long id) {
        return toResponse(findProduct(id));
    }

    public ProductResponseDto update(Long id, ProductRequestDto request) {
        Product product = findProduct(id);
        product.update(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.category(),
                request.color());
        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        productRepository.delete(findProduct(id));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponseDto toResponse(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getColor());
    }
}
