package ir.ac.sharif.hotel.domain.model.room;
import java.io.Serializable;

public class PresidentialRoom extends Room  implements Serializable{
    private static final long serialVersionUID = 1L;
    public PresidentialRoom(int id, int beds, int floor) {
        super(id, beds, floor);
    }

    @Override public RoomType getType() { return RoomType.PRESIDENTIAL; }

    @Override protected long basePerBedPerNight() { return 400; }

    @Override public long calculateStayCost(int nights) {
        return calcByBedsAndNights(nights);
    }
}
