package net.thesphynx.srv.service;

import net.thesphynx.srv.dto.ServiceRequest;
import net.thesphynx.srv.dto.ServiceResponse;
import net.thesphynx.srv.entity.BookingStatus;
import net.thesphynx.srv.entity.ServiceOffer;
import net.thesphynx.srv.entity.ServiceStatus;
import net.thesphynx.srv.exception.ApiException;
import net.thesphynx.srv.repository.BookingRepository;
import net.thesphynx.srv.repository.ServiceOfferRepository;
import net.thesphynx.srv.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceOfferService {

    /** Réservations comptées sur la fiche d'un service (en cours ou terminées). */
    private static final List<BookingStatus> COUNTED =
            List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED, BookingStatus.COMPLETED);
    private static final List<BookingStatus> OPEN = List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED);

    private final ServiceOfferRepository repository;
    private final BookingRepository bookings;

    public ServiceOfferService(ServiceOfferRepository repository, BookingRepository bookings) {
        this.repository = repository;
        this.bookings = bookings;
    }

    /** Catalogue public : services actifs, filtrés (texte, catégorie, prix), plus récents d'abord. */
    @Transactional(readOnly = true)
    public List<ServiceResponse> search(String q, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        String text = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        String cat = category == null ? "" : category.trim();
        Map<Long, Long> counts = bookingCounts();
        return repository.findAll().stream()
                .filter(s -> s.getStatus() != ServiceStatus.PAUSED)
                .filter(s -> text.isEmpty() || contains(s.getName(), text) || contains(s.getDescription(), text)
                        || contains(s.getCategory(), text) || contains(s.getProviderName(), text))
                .filter(s -> cat.isEmpty() || cat.equalsIgnoreCase(s.getCategory()))
                .filter(s -> minPrice == null || (s.getPrice() != null && s.getPrice().compareTo(minPrice) >= 0))
                .filter(s -> maxPrice == null || (s.getPrice() != null && s.getPrice().compareTo(maxPrice) <= 0))
                .sorted(Comparator.comparing(ServiceOffer::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(s -> ServiceResponse.of(s, counts.getOrDefault(s.getId(), 0L)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> categories() {
        return repository.findAll().stream()
                .filter(s -> s.getStatus() != ServiceStatus.PAUSED)
                .map(ServiceOffer::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .map(String::trim)
                .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)))
                .stream().toList();
    }

    /** Fiche d'un service ; un service en pause n'est visible que par son prestataire. */
    @Transactional(readOnly = true)
    public ServiceResponse get(Long id, CurrentUser user) {
        ServiceOffer s = find(id);
        if (s.getStatus() == ServiceStatus.PAUSED && !isOwner(s, user)) {
            throw ApiException.notFound("Service introuvable : " + id);
        }
        return ServiceResponse.of(s, bookingCounts().getOrDefault(s.getId(), 0L));
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> mine(CurrentUser user) {
        Map<Long, Long> counts = bookingCounts();
        return repository.findByProviderIdOrderByCreatedAtDesc(user.id()).stream()
                .map(s -> ServiceResponse.of(s, counts.getOrDefault(s.getId(), 0L)))
                .toList();
    }

    public ServiceResponse create(ServiceRequest req, CurrentUser user) {
        ServiceOffer s = new ServiceOffer();
        apply(req, s);
        s.setProviderId(user.id());
        s.setProviderName(user.name());
        s.setProviderEmail(user.email());
        return ServiceResponse.of(repository.save(s), 0);
    }

    public ServiceResponse update(Long id, ServiceRequest req, CurrentUser user) {
        ServiceOffer s = ownedBy(id, user);
        apply(req, s);
        s.setProviderName(user.name());
        s.setProviderEmail(user.email());
        return ServiceResponse.of(repository.save(s), bookingCounts().getOrDefault(s.getId(), 0L));
    }

    /** Suppression refusée tant que des réservations sont en cours : il faut d'abord les clore (ou mettre en pause). */
    public void delete(Long id, CurrentUser user) {
        ServiceOffer s = ownedBy(id, user);
        if (bookings.existsByServiceIdAndStatusIn(id, OPEN)) {
            throw ApiException.conflict("Des réservations sont en cours sur ce service : mettez-le en pause ou traitez-les avant de le supprimer");
        }
        bookings.deleteAll(bookings.findByServiceId(id));
        repository.delete(s);
    }

    ServiceOffer find(Long id) {
        return repository.findById(id).orElseThrow(() -> ApiException.notFound("Service introuvable : " + id));
    }

    private ServiceOffer ownedBy(Long id, CurrentUser user) {
        ServiceOffer s = find(id);
        if (!isOwner(s, user)) {
            throw ApiException.forbidden("Seul le prestataire de ce service peut le modifier");
        }
        return s;
    }

    private static boolean isOwner(ServiceOffer s, CurrentUser user) {
        return user != null && user.id() != null && user.id().equals(s.getProviderId());
    }

    private static void apply(ServiceRequest req, ServiceOffer s) {
        s.setName(req.name().trim());
        s.setDescription(blankToNull(req.description()));
        s.setCategory(blankToNull(req.category()));
        s.setPrice(req.price());
        s.setDurationMinutes(req.durationMinutes());
        s.setMode(req.mode());
        s.setLocation(blankToNull(req.location()));
        s.setImageUrl(blankToNull(req.imageUrl()));
        if (req.status() != null) s.setStatus(req.status());
    }

    private Map<Long, Long> bookingCounts() {
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : bookings.countByServiceAndStatusIn(COUNTED)) {
            counts.put((Long) row[0], (Long) row[1]);
        }
        return counts;
    }

    private static boolean contains(String value, String text) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(text);
    }

    private static String blankToNull(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }
}
