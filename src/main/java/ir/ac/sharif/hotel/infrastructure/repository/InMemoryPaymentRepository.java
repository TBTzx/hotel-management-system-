package ir.ac.sharif.hotel.infrastructure.repository;

import ir.ac.sharif.hotel.domain.model.finance.Payment;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPaymentRepository implements PaymentRepository {
    private final ConcurrentHashMap<String, Payment> byReservation = new ConcurrentHashMap<>();

    @Override
    public void save(Payment payment) {
        byReservation.put(payment.getReservationId(), payment);
    }

    @Override
    public Optional<Payment> findByReservationId(String reservationId) {
        return Optional.ofNullable(byReservation.get(reservationId));
    }


@Override
public List<Payment> findAll() {
    return new java.util.ArrayList<>(byReservation.values());
}

public synchronized void clearAndLoad(java.util.Collection<Payment> payments) {
    byReservation.clear();
    for (Payment p : payments) {
        byReservation.put(p.getReservationId(), p);
    }
}
}
