package net.thesphynx.delivery.repository;

import net.thesphynx.delivery.entity.DeliveryFee;
import net.thesphynx.delivery.enums.DeliveryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface DeliveryFeeRepository extends JpaRepository<DeliveryFee, DeliveryType> {
}
