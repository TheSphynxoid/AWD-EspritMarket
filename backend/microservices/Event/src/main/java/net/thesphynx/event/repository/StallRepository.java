package net.thesphynx.event.repository;

import net.thesphynx.event.entity.Stall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StallRepository extends JpaRepository<Stall, Long> {
}
