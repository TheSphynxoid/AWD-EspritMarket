package net.thesphynx.patnership.repository;

import net.thesphynx.patnership.entity.PartnerCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerCompanyRepository extends JpaRepository<PartnerCompany, Long> {

    List<PartnerCompany> findByPartnershipStatus(String partnershipStatus);
}
