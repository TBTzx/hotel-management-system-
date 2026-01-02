package ir.ac.sharif.hotel.application.service;

import ir.ac.sharif.hotel.domain.exception.NotFoundException;
import ir.ac.sharif.hotel.domain.exception.ValidationException;
import ir.ac.sharif.hotel.domain.model.finance.Invoice;
import ir.ac.sharif.hotel.domain.model.finance.Payment;
import ir.ac.sharif.hotel.domain.model.reservation.Reservation;
import ir.ac.sharif.hotel.infrastructure.repository.PaymentRepository;
import ir.ac.sharif.hotel.infrastructure.repository.ReservationRepository;

import java.util.Objects;

public class PaymentService {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(ReservationRepository reservationRepository, PaymentRepository paymentRepository) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository, "reservationRepository");
        this.paymentRepository = Objects.requireNonNull(paymentRepository, "paymentRepository");
    }

    public Payment pay(String reservationId, Invoice invoice, long amount) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        if (r.isPaid()) throw new ValidationException("Reservation already paid");
        if (amount < invoice.getFinalPayable()) throw new ValidationException("Insufficient payment amount");

        Payment p = new Payment(reservationId, amount);
        paymentRepository.save(p);

        r.markPaid(p.getId());
        reservationRepository.save(r);
        return p;
    }
}
