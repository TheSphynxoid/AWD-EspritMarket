package net.thesphynx.srv.repository;

import net.thesphynx.srv.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(String bookerId);
    List<Booking> findByProviderId(String providerId);
}
