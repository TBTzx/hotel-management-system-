package ir.ac.sharif.hotel.domain.model.reservation;
import java.io.Serializable;
import ir.ac.sharif.hotel.domain.model.guest.Guest;
import ir.ac.sharif.hotel.domain.model.service.ServiceBooking;
import ir.ac.sharif.hotel.domain.model.user.User;

import java.time.LocalDate;
import java.util.*;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String username; // رزروکننده
    private final List<Integer> roomIds;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private ReservationStatus status;

    private final List<Guest> guests;
    private final List<ServiceBooking> serviceBookings;

    // Phase 2: payment
    private boolean paid;
    private String paymentId;

    public Reservation(User user, List<Integer> roomIds, LocalDate startDate, LocalDate endDate) {
        this.id = UUID.randomUUID().toString();
        this.username = Objects.requireNonNull(user, "user").getUsername();
        this.roomIds = List.copyOf(Objects.requireNonNull(roomIds, "roomIds"));
        this.startDate = Objects.requireNonNull(startDate, "startDate");
        this.endDate = Objects.requireNonNull(endDate, "endDate");
        this.status = ReservationStatus.ACTIVE;

        this.guests = new ArrayList<>();
        this.serviceBookings = new ArrayList<>();

        this.paid = false;
        this.paymentId = null;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public List<Integer> getRoomIds() { return roomIds; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public ReservationStatus getStatus() { return status; }

    public List<Guest> getGuests() { return Collections.unmodifiableList(guests); }
    public List<ServiceBooking> getServiceBookings() { return Collections.unmodifiableList(serviceBookings); }

    public boolean isPaid() { return paid; }
    public String getPaymentId() { return paymentId; }

    public void cancel() { this.status = ReservationStatus.CANCELED; }

    public void addGuest(Guest guest) { guests.add(Objects.requireNonNull(guest, "guest")); }
    public void addServiceBooking(ServiceBooking booking) { serviceBookings.add(Objects.requireNonNull(booking, "booking")); }
    public void clearServiceBookings() { serviceBookings.clear(); }

    public void markPaid(String paymentId) {
        this.paid = true;
        this.paymentId = Objects.requireNonNull(paymentId, "paymentId");
    }
}
