package ir.ac.sharif.hotel.domain.model.service;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class TimeSlot implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = Objects.requireNonNull(start, "start");
        this.end = Objects.requireNonNull(end, "end");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("TimeSlot end must be after start");
        }
    }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end) && other.start.isBefore(end);
    }
}
