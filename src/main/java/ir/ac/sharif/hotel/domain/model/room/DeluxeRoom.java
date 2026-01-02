package ir.ac.sharif.hotel.domain.model.room;
import java.io.Serializable;

public class DeluxeRoom extends Room  implements Serializable{
    private static final long serialVersionUID = 1L;
    public DeluxeRoom(int id, int beds, int floor) {
        super(id, beds, floor);
    }

    @Override public RoomType getType() { return RoomType.DELUXE; }

    @Override protected long basePerBedPerNight() { return 220; }

    @Override public long calculateStayCost(int nights) {
        return calcByBedsAndNights(nights);
    }
}
