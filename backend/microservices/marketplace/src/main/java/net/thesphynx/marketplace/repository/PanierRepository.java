package net.thesphynx.marketplace.repository;

import java.util.Optional;
import net.thesphynx.marketplace.entity.Panier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PanierRepository extends JpaRepository<Panier, Long> {
    Optional<Panier> findByUserId(String userId);
}
