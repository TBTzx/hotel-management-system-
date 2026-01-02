package ir.ac.sharif.hotel.domain.model.room;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * طبق PDF: Room باید abstract باشد و برای انواع اتاق از آن ارث‌بری شود.
 * همچنین محاسبه هزینه در زیرکلاس‌ها override می‌شود.
 */
public abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final int beds;
    private final int floor;
    private RoomStatus status;
    private LocalDate vacateDate; // فقط وقتی OCCUPIED است معنی‌دار است.

    protected Room(int id, int beds, int floor) {
        this.id = id;
        this.beds = beds;
        this.floor = floor;
        this.status = RoomStatus.AVAILABLE;
        this.vacateDate = null;
    }

    public int getId() { return id; }
    public int getBeds() { return beds; }
    public int getFloor() { return floor; }
    public RoomStatus getStatus() { return status; }
    public LocalDate getVacateDate() { return vacateDate; }

    public void setStatus(RoomStatus status) {
        this.status = Objects.requireNonNull(status, "status");
        if (status != RoomStatus.OCCUPIED) {
            this.vacateDate = null;
        }
    }

    public void setOccupiedUntil(LocalDate vacateDate) {
        this.status = RoomStatus.OCCUPIED;
        this.vacateDate = Objects.requireNonNull(vacateDate, "vacateDate");
    }

    public abstract RoomType getType();

    /**
     * هزینه اقامت برای این اتاق (برای تعداد شب‌ها).
     * سیاست دقیق قیمت‌گذاری در PDF عدد نداده؛ پس طبق نوع اتاق override می‌شود و مبتنی بر beds و nights است.
     */
    public abstract long calculateStayCost(int nights);

    protected long basePerBedPerNight() {
        return 100; // پیش‌فرض؛ هر زیرکلاس override می‌کند.
    }

    protected final long calcByBedsAndNights(int nights) {
        if (nights <= 0) return 0;
        return basePerBedPerNight() * (long) beds * (long) nights;
    }
}
