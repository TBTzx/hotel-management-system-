package ir.ac.sharif.hotel.application.service;

import ir.ac.sharif.hotel.domain.exception.NotFoundException;
import ir.ac.sharif.hotel.domain.exception.ValidationException;
import ir.ac.sharif.hotel.domain.model.guest.Guest;
import ir.ac.sharif.hotel.domain.model.reservation.Reservation;
import ir.ac.sharif.hotel.domain.model.reservation.ReservationStatus;
import ir.ac.sharif.hotel.infrastructure.repository.ReservationRepository;

import java.util.Objects;

public class GuestService {
    private final ReservationRepository reservationRepository;

    public GuestService(ReservationRepository reservationRepository) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository, "reservationRepository");
    }


    public void addGuest(String reservationId, Guest guest) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
        if (r.getStatus() == ReservationStatus.CANCELED) throw new ValidationException("Reservation is canceled");

        int limit = 3 * r.getRoomIds().size();
        if (r.getGuests().size() >= limit) throw new ValidationException("Guest limit exceeded (max " + limit + ")");

        r.addGuest(guest);
        reservationRepository.save(r);
    }
}
