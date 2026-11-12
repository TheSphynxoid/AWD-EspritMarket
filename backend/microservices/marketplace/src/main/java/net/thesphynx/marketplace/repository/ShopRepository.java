package net.thesphynx.marketplace.repository;

import java.util.List;
import net.thesphynx.marketplace.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByOwnerId(String ownerId);
    boolean existsByName(String name);
}
