package net.thesphynx.srv.repository;

import net.thesphynx.srv.entity.ServiceOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOfferRepository extends JpaRepository<ServiceOffer, Long> {
}
