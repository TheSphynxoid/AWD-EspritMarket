package net.thesphynx.patnership.repository;

import net.thesphynx.patnership.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobOfferId(Long jobOfferId);

    List<Application> findByUserId(Long userId);
}
