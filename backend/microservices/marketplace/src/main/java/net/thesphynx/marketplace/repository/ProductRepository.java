package net.thesphynx.marketplace.repository;

import net.thesphynx.marketplace.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
