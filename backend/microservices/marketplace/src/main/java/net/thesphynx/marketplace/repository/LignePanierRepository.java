package net.thesphynx.marketplace.repository;

import java.util.Optional;
import net.thesphynx.marketplace.entity.LignePanier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LignePanierRepository extends JpaRepository<LignePanier, Long> {
    Optional<LignePanier> findByPanierIdAndProductId(Long panierId, Long productId);
}
