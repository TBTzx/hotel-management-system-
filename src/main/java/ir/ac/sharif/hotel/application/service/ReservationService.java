package ir.ac.sharif.hotel.application.service;

import ir.ac.sharif.hotel.application.dto.RoomHistoryEntryDto;

import ir.ac.sharif.hotel.application.policy.AccessPolicy;
import ir.ac.sharif.hotel.domain.exception.ConflictException;
import ir.ac.sharif.hotel.domain.exception.NotFoundException;
import ir.ac.sharif.hotel.domain.exception.ValidationException;
import ir.ac.sharif.hotel.domain.model.reservation.PenaltyPolicy;
import ir.ac.sharif.hotel.domain.model.reservation.Reservation;
import ir.ac.sharif.hotel.domain.model.reservation.ReservationStatus;
import ir.ac.sharif.hotel.domain.model.room.Room;
import ir.ac.sharif.hotel.domain.model.room.RoomStatus;
import ir.ac.sharif.hotel.domain.model.user.Role;
import ir.ac.sharif.hotel.domain.model.user.User;
import ir.ac.sharif.hotel.infrastructure.repository.ReservationRepository;
import ir.ac.sharif.hotel.infrastructure.repository.RoomRepository;
import ir.ac.sharif.hotel.infrastructure.repository.ServiceBookingRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ReservationService {
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final PenaltyPolicy penaltyPolicy;

    public ReservationService(RoomRepository roomRepository,
                              ReservationRepository reservationRepository,
                              ServiceBookingRepository serviceBookingRepository,
                              PenaltyPolicy penaltyPolicy) {
        this.roomRepository = Objects.requireNonNull(roomRepository, "roomRepository");
        this.reservationRepository = Objects.requireNonNull(reservationRepository, "reservationRepository");
        this.serviceBookingRepository = Objects.requireNonNull(serviceBookingRepository, "serviceBookingRepository");
        this.penaltyPolicy = Objects.requireNonNull(penaltyPolicy, "penaltyPolicy");
    }

    public Reservation reserveRooms(User user, List<Integer> roomIds, LocalDate start, LocalDate end) {
        if (user == null) throw new ValidationException("Not authenticated");
        if (roomIds == null || roomIds.isEmpty()) throw new ValidationException("No rooms selected");
        if (start == null || end == null || !end.isAfter(start)) throw new ValidationException("Invalid date range");

        for (int id : roomIds) {
            Room room = roomRepository.findById(id).orElseThrow(() -> new NotFoundException("Room not found: " + id));
            if (!(room.getStatus() == RoomStatus.AVAILABLE || room.getStatus() == RoomStatus.CLEANING)) {
                throw new ConflictException("Room is not reservable (must be AVAILABLE or CLEANING): " + id);
            }
        }

        Reservation r = new Reservation(user, roomIds, start, end);
        reservationRepository.save(r);

        for (int id : roomIds) {
            Room room = roomRepository.findById(id).orElseThrow();
            room.setOccupiedUntil(end);
            roomRepository.save(room);
        }
        return r;
    }


    public Reservation reserveRoomsAndPay(User user,
                                         List<Integer> roomIds,
                                         LocalDate start,
                                         LocalDate end,
                                         BillingService billingService,
                                         PaymentService paymentService,
                                         int pastReservationsCount,
                                         long paidAmount) {
        Reservation r = reserveRooms(user, roomIds, start, end);
        var invoice = billingService.calculateInvoice(r.getId(), pastReservationsCount);
        paymentService.pay(r.getId(), invoice, paidAmount);
        return reservationRepository.findById(r.getId()).orElseThrow();
    }


    public long cancelReservation(User user, String reservationId) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        if (user == null) throw new ValidationException("Not authenticated");
        if (!user.getUsername().equals(r.getUsername()) && user.getRole() != Role.ADMIN_MAIN) {
            throw new ValidationException("You cannot cancel this reservation");
        }
        if (r.getStatus() == ReservationStatus.CANCELED) return 0;

        LocalDate today = LocalDate.now();
        double rate = penaltyPolicy.penaltyRate(today, r.getStartDate());

        long nights = ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
        long base = 0;
        for (int roomId : r.getRoomIds()) {
            Room room = roomRepository.findById(roomId).orElseThrow();
            base += room.calculateStayCost((int) nights);
        }
        long penalty = Math.round(base * rate);

        r.cancel();
        reservationRepository.save(r);

        for (int id : r.getRoomIds()) {
            Room room = roomRepository.findById(id).orElseThrow();
            room.setStatus(RoomStatus.CLEANING);
            roomRepository.save(room);
        }

        serviceBookingRepository.deleteByReservationId(r.getId());

        return penalty;
    }

    public List<Reservation> historyForUser(User user) {
        if (user == null) throw new ValidationException("Not authenticated");
        List<Reservation> out = new ArrayList<>();
        for (Reservation r : reservationRepository.findAll()) {
            if (r.getUsername().equals(user.getUsername())) out.add(r);
        }
        return out;
    }

    public List<Reservation> historyByUsername(User actor, String username) {
        AccessPolicy.requireAny(actor, Role.ADMIN_MAIN, Role.ADMIN_RESERVATION);
        List<Reservation> out = new ArrayList<>();
        for (Reservation r : reservationRepository.findAll()) {
            if (r.getUsername().equals(username)) out.add(r);
        }
        return out;
    }

    public List<Reservation> allReservations(User actor) {
        AccessPolicy.requireAny(actor, Role.ADMIN_MAIN, Role.ADMIN_RESERVATION);
        return reservationRepository.findAll();
    }


public java.util.List<RoomHistoryEntryDto> roomHistory(User actor, int roomId) {
    if (actor == null) throw new ValidationException("Not authenticated");

    roomRepository.findById(roomId).orElseThrow(() -> new NotFoundException("Room not found: " + roomId));

    java.util.List<RoomHistoryEntryDto> out = new java.util.ArrayList<>();
    for (Reservation r : reservationRepository.findAll()) {
        if (r.getRoomIds().contains(roomId)) {
            out.add(new RoomHistoryEntryDto(
                    r.getId(),
                    r.getUsername(),
                    roomId,
                    r.getStartDate(),
                    r.getEndDate(),
                    r.getStatus(),
                    r.isPaid()
            ));
        }
    }
    out.sort(java.util.Comparator.comparing(RoomHistoryEntryDto::startDate).reversed());
    return out;
}
}
