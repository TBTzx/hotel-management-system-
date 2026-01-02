package ir.ac.sharif.hotel.infrastructure.repository;

import ir.ac.sharif.hotel.domain.model.reservation.Reservation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryReservationRepository implements ReservationRepository {
    private final Map<String, Reservation> data = new ConcurrentHashMap<>();

    @Override
    public void save(Reservation reservation) {
        data.put(reservation.getId(), reservation);
    }

    @Override
    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(data.values());
    }

/** برای بکاپ/ریستور: ریست و بارگذاری کل رزروها */
public synchronized void clearAndLoad(java.util.Collection<Reservation> reservations) {
    data.clear();
    for (Reservation r : reservations) {
        save(r);
    }
}
}
