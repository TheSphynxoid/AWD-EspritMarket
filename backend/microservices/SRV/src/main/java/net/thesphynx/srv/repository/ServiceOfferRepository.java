package net.thesphynx.srv.repository;

import net.thesphynx.srv.entity.ServiceOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOfferRepository extends JpaRepository<ServiceOffer, Long> {
    List<ServiceOffer> findByProviderIdOrderByCreatedAtDesc(String providerId);
}
