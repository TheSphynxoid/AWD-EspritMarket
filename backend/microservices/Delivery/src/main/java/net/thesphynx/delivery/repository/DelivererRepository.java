package net.thesphynx.delivery.repository;

import net.thesphynx.delivery.entity.Deliverer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(exported = false)
public interface DelivererRepository extends JpaRepository<Deliverer, Long> {

    List<Deliverer> findByVehicleId(Long vehicleId);
}
