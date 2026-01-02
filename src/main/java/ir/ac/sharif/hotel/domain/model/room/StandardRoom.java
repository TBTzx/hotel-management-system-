package ir.ac.sharif.hotel.domain.model.room;
import java.io.Serializable;

public class StandardRoom extends Room  implements Serializable{
    private static final long serialVersionUID = 1L;
    public StandardRoom(int id, int beds, int floor) {
        super(id, beds, floor);
    }

    @Override public RoomType getType() { return RoomType.STANDARD; }

    @Override protected long basePerBedPerNight() { return 100; }

    @Override public long calculateStayCost(int nights) {
        return calcByBedsAndNights(nights);
    }
}
