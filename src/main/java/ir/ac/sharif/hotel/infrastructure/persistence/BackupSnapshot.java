package ir.ac.sharif.hotel.infrastructure.persistence;

import ir.ac.sharif.hotel.domain.model.finance.Payment;
import ir.ac.sharif.hotel.domain.model.reservation.Reservation;
import ir.ac.sharif.hotel.domain.model.room.Room;
import ir.ac.sharif.hotel.domain.model.service.ServiceBooking;
import ir.ac.sharif.hotel.domain.model.user.User;

import java.io.Serializable;
import java.util.List;


public class BackupSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<User> users;
    private final List<Room> rooms;
    private final List<Reservation> reservations;
    private final List<ServiceBooking> serviceBookings;
    private final List<Payment> payments;

    public BackupSnapshot(List<User> users,
                          List<Room> rooms,
                          List<Reservation> reservations,
                          List<ServiceBooking> serviceBookings,
                          List<Payment> payments) {
        this.users = users;
        this.rooms = rooms;
        this.reservations = reservations;
        this.serviceBookings = serviceBookings;
        this.payments = payments;
    }

    public List<User> getUsers() { return users; }
    public List<Room> getRooms() { return rooms; }
    public List<Reservation> getReservations() { return reservations; }
    public List<ServiceBooking> getServiceBookings() { return serviceBookings; }
    public List<Payment> getPayments() { return payments; }
}
