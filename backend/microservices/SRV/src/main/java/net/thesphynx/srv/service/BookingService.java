package net.thesphynx.srv.service;

import net.thesphynx.srv.dto.BookingRequest;
import net.thesphynx.srv.dto.BookingResponse;
import net.thesphynx.srv.dto.BookingStatusRequest;
import net.thesphynx.srv.entity.Booking;
import net.thesphynx.srv.entity.BookingStatus;
import net.thesphynx.srv.entity.ServiceOffer;
import net.thesphynx.srv.entity.ServiceStatus;
import net.thesphynx.srv.exception.ApiException;
import net.thesphynx.srv.repository.BookingRepository;
import net.thesphynx.srv.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Réservations : le client réserve, le prestataire accepte / refuse puis termine,
 * le client peut annuler tant que la prestation n'est pas faite.
 * Chaque étape notifie l'autre partie (RabbitMQ -> Notification).
 */
@Service
@Transactional
public class BookingService {

    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final BookingRepository bookings;
    private final ServiceOfferService services;
    private final NotificationPublisher notifications;

    public BookingService(BookingRepository bookings, ServiceOfferService services, NotificationPublisher notifications) {
        this.bookings = bookings;
        this.services = services;
        this.notifications = notifications;
    }

    public BookingResponse create(BookingRequest req, CurrentUser user) {
        ServiceOffer service = services.find(req.serviceId());
        if (service.getStatus() == ServiceStatus.PAUSED) {
            throw ApiException.conflict("Ce service est en pause et ne prend pas de réservation");
        }
        if (user.id().equals(service.getProviderId())) {
            throw ApiException.conflict("Vous ne pouvez pas réserver votre propre service");
        }
        Booking booking = Booking.builder()
                .service(service)
                .providerId(service.getProviderId())
                .bookerId(user.id())
                .bookerName(user.name())
                .bookerEmail(user.email())
                .scheduledAt(req.scheduledAt())
                .note(req.note() == null || req.note().isBlank() ? null : req.note().trim())
                .status(BookingStatus.PENDING)
                .build();
        Booking saved = bookings.save(booking);

        String when = saved.getScheduledAt().format(WHEN);
        notifications.send(service.getProviderEmail(), service.getProviderId(),
                "Nouvelle réservation de " + user.name() + " pour « " + service.getName() + " » le " + when + ".");
        notifications.send(user.email(), user.id(),
                "Votre demande pour « " + service.getName() + " » le " + when + " a été envoyée au prestataire.");
        return BookingResponse.of(saved);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> mine(CurrentUser user) {
        return bookings.findByBookerIdOrderByScheduledAtDesc(user.id()).stream().map(BookingResponse::of).toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> received(CurrentUser user, BookingStatus status) {
        return bookings.findByProviderIdOrderByScheduledAtDesc(user.id()).stream()
                .filter(b -> status == null || b.getStatus() == status)
                .map(BookingResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse get(Long id, CurrentUser user) {
        Booking b = find(id);
        if (!user.id().equals(b.getBookerId()) && !user.id().equals(b.getProviderId())) {
            throw ApiException.forbidden("Cette réservation ne vous concerne pas");
        }
        return BookingResponse.of(b);
    }

    public BookingResponse changeStatus(Long id, BookingStatusRequest req, CurrentUser user) {
        Booking b = find(id);
        boolean isProvider = user.id().equals(b.getProviderId());
        boolean isBooker = user.id().equals(b.getBookerId());
        if (!isProvider && !isBooker) {
            throw ApiException.forbidden("Cette réservation ne vous concerne pas");
        }
        BookingStatus from = b.getStatus();
        BookingStatus to = req.status();

        boolean allowed = switch (to) {
            case ACCEPTED, DECLINED -> isProvider && from == BookingStatus.PENDING;
            case COMPLETED -> isProvider && from == BookingStatus.ACCEPTED;
            case CANCELLED -> isBooker && (from == BookingStatus.PENDING || from == BookingStatus.ACCEPTED);
            case PENDING -> false;
        };
        if (!allowed) {
            if ((to == BookingStatus.CANCELLED && !isBooker) || (to != BookingStatus.CANCELLED && !isProvider)) {
                throw ApiException.forbidden(isProvider
                        ? "Le prestataire ne peut pas passer une réservation à " + to
                        : "Le client ne peut pas passer une réservation à " + to);
            }
            throw ApiException.conflict("Transition impossible : " + from + " -> " + to);
        }

        b.setStatus(to);
        if (isProvider && req.note() != null && !req.note().isBlank()) {
            b.setProviderNote(req.note().trim());
        }
        Booking saved = bookings.save(b);
        notifyStatus(saved, user);
        return BookingResponse.of(saved);
    }

    private void notifyStatus(Booking b, CurrentUser actor) {
        ServiceOffer s = b.getService();
        String name = s != null ? s.getName() : "votre service";
        String when = b.getScheduledAt() != null ? " du " + b.getScheduledAt().format(WHEN) : "";
        String note = b.getProviderNote() != null && b.getStatus() != BookingStatus.CANCELLED
                ? " Message du prestataire : " + b.getProviderNote() : "";
        switch (b.getStatus()) {
            case ACCEPTED -> notifications.send(b.getBookerEmail(), b.getBookerId(),
                    "Votre réservation « " + name + " »" + when + " a été acceptée." + note);
            case DECLINED -> notifications.send(b.getBookerEmail(), b.getBookerId(),
                    "Votre réservation « " + name + " »" + when + " a été refusée." + note);
            case COMPLETED -> notifications.send(b.getBookerEmail(), b.getBookerId(),
                    "La prestation « " + name + " »" + when + " est marquée comme terminée. Merci !");
            case CANCELLED -> notifications.send(s != null ? s.getProviderEmail() : null, b.getProviderId(),
                    actor.name() + " a annulé sa réservation « " + name + " »" + when + ".");
            default -> { }
        }
    }

    private Booking find(Long id) {
        return bookings.findById(id).orElseThrow(() -> ApiException.notFound("Réservation introuvable : " + id));
    }
}
