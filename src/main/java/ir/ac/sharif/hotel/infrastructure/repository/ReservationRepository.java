package ir.ac.sharif.hotel.infrastructure.repository;

import ir.ac.sharif.hotel.domain.model.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    void save(Reservation reservation);
    Optional<Reservation> findById(String id);
    List<Reservation> findAll();
}
