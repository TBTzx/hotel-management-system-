package ir.ac.sharif.hotel.infrastructure.repository;

import ir.ac.sharif.hotel.domain.model.service.ServiceBooking;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryServiceBookingRepository implements ServiceBookingRepository {
    private final List<ServiceBooking> data = new CopyOnWriteArrayList<>();

    @Override
    public void save(ServiceBooking booking) {
        data.add(booking);
    }

    @Override
    public List<ServiceBooking> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public List<ServiceBooking> findByReservationId(String reservationId) {
        List<ServiceBooking> out = new ArrayList<>();
        for (ServiceBooking b : data) {
            if (b.getReservationId().equals(reservationId)) out.add(b);
        }
        return out;
    }

    @Override
    public void deleteByReservationId(String reservationId) {
        data.removeIf(b -> b.getReservationId().equals(reservationId));
    }

public synchronized void clearAndLoad(java.util.Collection<ServiceBooking> bookings) {
    data.clear();
    data.addAll(bookings);
}
}
