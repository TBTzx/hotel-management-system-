package ir.ac.sharif.hotel.infrastructure.repository;

import ir.ac.sharif.hotel.domain.model.room.Room;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRoomRepository implements RoomRepository {
    private final Map<Integer, Room> rooms = new ConcurrentHashMap<>();

    @Override
    public void save(Room room) {
        rooms.put(room.getId(), room);
    }

    @Override
    public Optional<Room> findById(int id) {
        return Optional.ofNullable(rooms.get(id));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

public synchronized void clearAndLoad(java.util.Collection<Room> roomsList) {
    rooms.clear();
    for (Room r : roomsList) {
        save(r);
    }
}
}
