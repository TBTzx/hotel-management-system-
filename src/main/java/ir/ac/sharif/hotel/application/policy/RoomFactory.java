package ir.ac.sharif.hotel.application.policy;

import ir.ac.sharif.hotel.domain.model.room.*;

public final class RoomFactory {
    private RoomFactory() {}

    public static Room create(RoomType type, int id, int beds, int floor) {
        return switch (type) {
            case STANDARD -> new StandardRoom(id, beds, floor);
            case SUITE -> new SuiteRoom(id, beds, floor);
            case DELUXE -> new DeluxeRoom(id, beds, floor);
            case PRESIDENTIAL -> new PresidentialRoom(id, beds, floor);
        };
    }
}
