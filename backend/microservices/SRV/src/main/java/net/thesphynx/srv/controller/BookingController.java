package net.thesphynx.srv.controller;

import net.thesphynx.srv.entity.Booking;
import net.thesphynx.srv.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/srv/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> create(@RequestParam Long serviceId,
                                          @RequestParam String bookerId) {
        return ResponseEntity.ok(bookingService.create(serviceId, bookerId));
    }

    @GetMapping
    public List<Booking> list(@RequestParam(required = false) String bookerId,
                              @RequestParam(required = false) String providerId) {
        if (bookerId != null) return bookingService.findByBooker(bookerId);
        if (providerId != null) return bookingService.findByProvider(providerId);
        return List.of();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateStatus(@PathVariable Long id,
                                                @RequestParam String status) {
        return bookingService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
