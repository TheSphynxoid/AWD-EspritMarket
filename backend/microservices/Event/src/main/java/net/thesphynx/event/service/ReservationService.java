package net.thesphynx.event.service;

import net.thesphynx.event.dto.ReservationDto;
import net.thesphynx.event.entity.Reservation;
import net.thesphynx.event.repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventService eventService;

    public ReservationService(ReservationRepository reservationRepository, EventService eventService) {
        this.reservationRepository = reservationRepository;
        this.eventService = eventService;
    }

    public List<ReservationDto> findAll() {
        return reservationRepository.findAll().stream().map(this::toDto).toList();
    }

    public ReservationDto findById(Long id) {
        return toDto(getReservation(id));
    }

    public ReservationDto create(ReservationDto dto) {
        return toDto(reservationRepository.save(toEntity(dto)));
    }

    public ReservationDto update(Long id, ReservationDto dto) {
        Reservation reservation = getReservation(id);
        reservation.setDate(dto.getDate());
        reservation.setName(dto.getName());
        reservation.setEvent(eventService.getEvent(dto.getEventId()));
        return toDto(reservationRepository.save(reservation));
    }

    public void delete(Long id) {
        reservationRepository.delete(getReservation(id));
    }

    private Reservation getReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Réservation introuvable : " + id));
    }

    private Reservation toEntity(ReservationDto dto) {
        Reservation reservation = new Reservation();
        reservation.setDate(dto.getDate());
        reservation.setName(dto.getName());
        reservation.setEvent(eventService.getEvent(dto.getEventId()));
        return reservation;
    }

    private ReservationDto toDto(Reservation reservation) {
        return new ReservationDto(reservation.getId(), reservation.getDate(),
                reservation.getName(), reservation.getEvent().getId());
    }
}
