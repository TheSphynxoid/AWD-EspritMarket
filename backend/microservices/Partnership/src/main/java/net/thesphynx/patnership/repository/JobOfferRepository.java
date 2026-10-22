package net.thesphynx.patnership.repository;

import net.thesphynx.patnership.entity.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

    List<JobOffer> findByCompanyId(Long companyId);

    List<JobOffer> findByStatus(String status);
}
