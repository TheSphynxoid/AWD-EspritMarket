package net.thesphynx.srv.repository;

import net.thesphynx.srv.entity.Booking;
import net.thesphynx.srv.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByScheduledAtDesc(String bookerId);

    List<Booking> findByProviderIdOrderByScheduledAtDesc(String providerId);

    List<Booking> findByServiceId(Long serviceId);

    boolean existsByServiceIdAndStatusIn(Long serviceId, Collection<BookingStatus> statuses);

    /** Nombre de réservations dont le statut est dans la liste, par service : [serviceId, count]. */
    @Query("select b.service.id, count(b) from Booking b where b.status in :statuses group by b.service.id")
    List<Object[]> countByServiceAndStatusIn(@Param("statuses") Collection<BookingStatus> statuses);
}
