package ir.ac.sharif.hotel.application.service;

import ir.ac.sharif.hotel.application.dto.OccupantDto;
import ir.ac.sharif.hotel.application.policy.AccessPolicy;
import ir.ac.sharif.hotel.domain.model.reservation.Reservation;
import ir.ac.sharif.hotel.domain.model.reservation.ReservationStatus;
import ir.ac.sharif.hotel.domain.model.user.Role;
import ir.ac.sharif.hotel.domain.model.user.User;
import ir.ac.sharif.hotel.infrastructure.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HotelQueryService {
    private final ReservationRepository reservationRepository;

    public HotelQueryService(ReservationRepository reservationRepository) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository, "reservationRepository");
    }

    public List<OccupantDto> listCurrentOccupants(User actor) {
        AccessPolicy.requireAny(actor, Role.ADMIN_MAIN, Role.ADMIN_RESERVATION);
        LocalDate today = LocalDate.now();

        List<OccupantDto> out = new ArrayList<>();
        for (Reservation r : reservationRepository.findAll()) {
            if (r.getStatus() != ReservationStatus.ACTIVE) continue;
            if (today.isBefore(r.getStartDate()) || !today.isBefore(r.getEndDate())) continue; // [start, end)
            for (int roomId : r.getRoomIds()) {
                out.add(new OccupantDto(r.getUsername(), roomId));
            }
        }
        return out;
    }
}
