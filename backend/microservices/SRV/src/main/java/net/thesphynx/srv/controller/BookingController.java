package net.thesphynx.srv.controller;

import jakarta.validation.Valid;
import net.thesphynx.srv.dto.BookingRequest;
import net.thesphynx.srv.dto.BookingResponse;
import net.thesphynx.srv.dto.BookingStatusRequest;
import net.thesphynx.srv.entity.BookingStatus;
import net.thesphynx.srv.security.CurrentUser;
import net.thesphynx.srv.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Toutes les routes exigent un JWT : le client / prestataire est l'utilisateur connecté. */
@RestController
@RequestMapping("/srv/bookings")
public class BookingController {

    private final BookingService bookings;

    public BookingController(BookingService bookings) {
        this.bookings = bookings;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest req, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookings.create(req, CurrentUser.from(jwt)));
    }

    /** Réservations faites par l'utilisateur connecté (côté client). */
    @GetMapping("/mine")
    public List<BookingResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return bookings.mine(CurrentUser.from(jwt));
    }

    /** Réservations reçues sur ses services (côté prestataire), filtre optionnel ?status=PENDING */
    @GetMapping("/received")
    public List<BookingResponse> received(@RequestParam(required = false) BookingStatus status,
                                          @AuthenticationPrincipal Jwt jwt) {
        return bookings.received(CurrentUser.from(jwt), status);
    }

    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return bookings.get(id, CurrentUser.from(jwt));
    }

    /** { "status": "ACCEPTED" | "DECLINED" | "COMPLETED" | "CANCELLED", "note": "..." } */
    @PatchMapping("/{id}/status")
    public BookingResponse changeStatus(@PathVariable Long id, @Valid @RequestBody BookingStatusRequest req,
                                        @AuthenticationPrincipal Jwt jwt) {
        return bookings.changeStatus(id, req, CurrentUser.from(jwt));
    }
}
