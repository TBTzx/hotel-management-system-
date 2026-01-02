package ir.ac.sharif.hotel.infrastructure.repository;

import ir.ac.sharif.hotel.domain.model.service.ServiceBooking;

import java.util.List;

public interface ServiceBookingRepository {

    void save(ServiceBooking booking);

    List<ServiceBooking> findAll();

    List<ServiceBooking> findByReservationId(String reservationId);

    void deleteByReservationId(String reservationId);
}
