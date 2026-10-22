package net.thesphynx.marketplace.repository;

import java.util.List;
import net.thesphynx.marketplace.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = {"shop", "category"})
    List<Product> findByShopId(Long shopId);

    @EntityGraph(attributePaths = {"shop", "category"})
    List<Product> findAll();

    @EntityGraph(attributePaths = {"shop", "category"})
    java.util.Optional<Product> findById(Long id);

    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String name);
}
