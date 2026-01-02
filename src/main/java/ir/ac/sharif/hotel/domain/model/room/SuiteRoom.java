package ir.ac.sharif.hotel.domain.model.room;
import java.io.Serializable;

public class SuiteRoom extends Room  implements Serializable{
    private static final long serialVersionUID = 1L;
    public SuiteRoom(int id, int beds, int floor) {
        super(id, beds, floor);
    }

    @Override public RoomType getType() { return RoomType.SUITE; }

    @Override protected long basePerBedPerNight() { return 160; }

    @Override public long calculateStayCost(int nights) {
        return calcByBedsAndNights(nights);
    }
}
