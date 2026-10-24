package net.thesphynx.srv.service;

import net.thesphynx.srv.config.RabbitConfig;
import net.thesphynx.srv.entity.Booking;
import net.thesphynx.srv.entity.ServiceOffer;
import net.thesphynx.srv.repository.BookingRepository;
import net.thesphynx.srv.repository.ServiceOfferRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceOfferRepository serviceRepository;
    private final RabbitTemplate rabbitTemplate;

    public BookingService(BookingRepository bookingRepository,
                          ServiceOfferRepository serviceRepository,
                          RabbitTemplate rabbitTemplate) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Booking create(Long serviceId, String bookerId) {
        ServiceOffer service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        Booking booking = Booking.builder()
                .service(service)
                .providerId(service.getProviderId())
                .bookerId(bookerId)
                .build();
        Booking saved = bookingRepository.save(booking);

        Map<String, Object> message = Map.of(
                "recipient", "user" + bookerId + "@espritmarket.local",
                "message", "Your booking for service '" + service.getName() + "' has been created.",
                "type", "booking"
        );
        rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATION_QUEUE, message);

        return saved;
    }

    public List<Booking> findByBooker(Long bookerId) {
        return bookingRepository.findByBookerId(bookerId);
    }

    public List<Booking> findByProvider(Long providerId) {
        return bookingRepository.findByProviderId(providerId);
    }

    public Optional<Booking> updateStatus(Long id, String status) {
        return bookingRepository.findById(id).map(b -> {
            b.setStatus(status);
            return bookingRepository.save(b);
        });
    }
}
