package ir.ac.sharif.hotel.domain.model.finance;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String reservationId;
    private final long amount;
    private final Instant paidAt;

    public Payment(String reservationId, long amount) {
        this.id = UUID.randomUUID().toString();
        this.reservationId = Objects.requireNonNull(reservationId, "reservationId");
        this.amount = amount;
        this.paidAt = Instant.now();
    }

    public String getId() { return id; }
    public String getReservationId() { return reservationId; }
    public long getAmount() { return amount; }
    public Instant getPaidAt() { return paidAt; }
}
