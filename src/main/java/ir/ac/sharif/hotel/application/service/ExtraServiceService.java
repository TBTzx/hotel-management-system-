package ir.ac.sharif.hotel.application.service;

import ir.ac.sharif.hotel.application.dto.ConferenceBookingDto;
import ir.ac.sharif.hotel.application.policy.AccessPolicy;

import ir.ac.sharif.hotel.domain.exception.ConflictException;
import ir.ac.sharif.hotel.domain.exception.NotFoundException;
import ir.ac.sharif.hotel.domain.exception.ValidationException;
import ir.ac.sharif.hotel.domain.model.reservation.Reservation;
import ir.ac.sharif.hotel.domain.model.reservation.ReservationStatus;
import ir.ac.sharif.hotel.domain.model.service.ServiceBooking;
import ir.ac.sharif.hotel.domain.model.service.ServiceType;
import ir.ac.sharif.hotel.domain.model.service.TimeSlot;
import ir.ac.sharif.hotel.domain.model.user.Role;
import ir.ac.sharif.hotel.domain.model.user.User;
import ir.ac.sharif.hotel.infrastructure.repository.ReservationRepository;
import ir.ac.sharif.hotel.infrastructure.repository.ServiceBookingRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class ExtraServiceService {
    private final ReservationRepository reservationRepository;
    private final ServiceBookingRepository bookingRepository;

    public ExtraServiceService(ReservationRepository reservationRepository, ServiceBookingRepository bookingRepository) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository, "reservationRepository");
        this.bookingRepository = Objects.requireNonNull(bookingRepository, "bookingRepository");
    }

    public ServiceBooking book(User user, String reservationId, ServiceType type, TimeSlot slot) {
        if (user == null) throw new ValidationException("Not authenticated");

        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
        if (r.getStatus() == ReservationStatus.CANCELED) throw new ValidationException("Reservation is canceled");

        if (!user.getUsername().equals(r.getUsername()) && user.getRole() != ir.ac.sharif.hotel.domain.model.user.Role.ADMIN_MAIN) {
            throw new ValidationException("You cannot book service for this reservation");
        }

        if (type == ServiceType.CONFERENCE_HALL) {
            List<ServiceBooking> all = bookingRepository.findAll();
            for (ServiceBooking b : all) {
                if (b.getType() == ServiceType.CONFERENCE_HALL && b.getTimeSlot().overlaps(slot)) {
                    throw new ConflictException("Conference hall timeslot already booked");
                }
            }
        }

        long price = priceOf(type);
        ServiceBooking booking = new ServiceBooking(reservationId, type, slot, price);
        bookingRepository.save(booking);
        return booking;
    }

    public List<ServiceBooking> listForReservation(String reservationId) {
        return bookingRepository.findByReservationId(reservationId);
    }

    private long priceOf(ServiceType type) {
        return switch (type) {
            case POOL -> 50;
            case GYM -> 70;
            case CONFERENCE_HALL -> 300;
        };
    }

public List<ConferenceBookingDto> listConferenceHallBookings(User actor, boolean onlyUpcoming) {
    AccessPolicy.requireAny(actor, Role.ADMIN_MAIN, Role.ADMIN_SERVICES);

    LocalDateTime now = LocalDateTime.now();
    List<ConferenceBookingDto> out = new ArrayList<>();

    for (ServiceBooking b : bookingRepository.findAll()) {
        if (b.getType() != ServiceType.CONFERENCE_HALL) continue;

        boolean upcoming = b.getTimeSlot().getEnd().isAfter(now);
        if (onlyUpcoming && !upcoming) continue;

        String username = reservationRepository.findById(b.getReservationId())
                .map(Reservation::getUsername)
                .orElse("UNKNOWN");

        out.add(new ConferenceBookingDto(
                b.getId(),
                b.getReservationId(),
                username,
                b.getTimeSlot().getStart(),
                b.getTimeSlot().getEnd(),
                b.getPrice(),
                upcoming
        ));
    }

    out.sort(Comparator.comparing(ConferenceBookingDto::start));
    return out;
}

public List<ServiceBooking> listForReservationAsAdmin(User actor, String reservationId) {
    AccessPolicy.requireAny(actor, Role.ADMIN_MAIN, Role.ADMIN_SERVICES);
    return bookingRepository.findByReservationId(reservationId);
}
}
