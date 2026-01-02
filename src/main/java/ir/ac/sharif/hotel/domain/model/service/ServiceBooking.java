package ir.ac.sharif.hotel.domain.model.service;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ServiceBooking implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String reservationId;
    private final ServiceType type;
    private final TimeSlot timeSlot;
    private final long price;

    public ServiceBooking(String reservationId, ServiceType type, TimeSlot timeSlot, long price) {
        this.id = UUID.randomUUID().toString();
        this.reservationId = Objects.requireNonNull(reservationId, "reservationId");
        this.type = Objects.requireNonNull(type, "type");
        this.timeSlot = Objects.requireNonNull(timeSlot, "timeSlot");
        this.price = price;
    }

    public String getId() { return id; }
    public String getReservationId() { return reservationId; }
    public ServiceType getType() { return type; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public long getPrice() { return price; }
}
