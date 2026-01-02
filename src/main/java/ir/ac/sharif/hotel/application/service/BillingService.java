package ir.ac.sharif.hotel.application.service;

import ir.ac.sharif.hotel.domain.exception.NotFoundException;
import ir.ac.sharif.hotel.domain.model.finance.DiscountPolicy;
import ir.ac.sharif.hotel.domain.model.finance.Invoice;
import ir.ac.sharif.hotel.domain.model.reservation.Reservation;
import ir.ac.sharif.hotel.domain.model.reservation.ReservationStatus;
import ir.ac.sharif.hotel.domain.model.room.Room;
import ir.ac.sharif.hotel.infrastructure.repository.ReservationRepository;
import ir.ac.sharif.hotel.infrastructure.repository.RoomRepository;
import ir.ac.sharif.hotel.infrastructure.repository.ServiceBookingRepository;

import java.time.temporal.ChronoUnit;
import java.util.Objects;


public class BillingService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final DiscountPolicy discountPolicy;

    public BillingService(ReservationRepository reservationRepository,
                          RoomRepository roomRepository,
                          ServiceBookingRepository serviceBookingRepository,
                          DiscountPolicy discountPolicy) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository, "reservationRepository");
        this.roomRepository = Objects.requireNonNull(roomRepository, "roomRepository");
        this.serviceBookingRepository = Objects.requireNonNull(serviceBookingRepository, "serviceBookingRepository");
        this.discountPolicy = Objects.requireNonNull(discountPolicy, "discountPolicy");
    }

    public Invoice calculateInvoice(String reservationId, int pastReservationsCount) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        int nights = (int) ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate());
        if (nights < 0) nights = 0;

        Invoice inv = new Invoice();
        long roomsTotal = 0;
        for (int roomId : r.getRoomIds()) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new NotFoundException("Room not found: " + roomId));
            long cost = room.calculateStayCost(nights);
            inv.putRoomCost(roomId, cost);
            roomsTotal += cost;
        }

        long servicesTotal = serviceBookingRepository.findByReservationId(reservationId)
                .stream().mapToLong(b -> b.getPrice()).sum();
        inv.setServicesCost(servicesTotal);

        double rate = discountPolicy.discountRate(pastReservationsCount);
        if (rate > 0.40) rate = 0.40;

        long beforeDiscount = roomsTotal + servicesTotal;
        long finalPayable = Math.round(beforeDiscount * (1.0 - rate));

        inv.setDiscountRate(rate);
        inv.setFinalPayable(finalPayable);
        return inv;
    }

    public int pastReservationsCountForUser(String username) {
        int count = 0;
        for (Reservation r : reservationRepository.findAll()) {
            if (r.getUsername().equals(username) && r.getStatus() == ReservationStatus.ACTIVE) {
                count++;
            }
        }
        return count;
    }
}
