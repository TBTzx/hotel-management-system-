package ir.ac.sharif.hotel.application.service;

import ir.ac.sharif.hotel.application.dto.RoomDetailsDto;
import ir.ac.sharif.hotel.application.dto.RoomSummaryDto;
import ir.ac.sharif.hotel.application.policy.AccessPolicy;
import ir.ac.sharif.hotel.application.policy.RoomFactory;
import ir.ac.sharif.hotel.domain.exception.NotFoundException;
import ir.ac.sharif.hotel.domain.model.room.*;
import ir.ac.sharif.hotel.domain.model.user.Role;
import ir.ac.sharif.hotel.domain.model.user.User;
import ir.ac.sharif.hotel.infrastructure.repository.RoomRepository;

import java.util.*;
import java.util.stream.Collectors;

public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = Objects.requireNonNull(roomRepository, "roomRepository");
    }

    public void addRoom(User actor, RoomType type, int id, int beds, int floor) {
        AccessPolicy.requireAny(actor, Role.ADMIN_MAIN); // ایجاد اتاق را فقط ادمین اصلی انجام دهد.
        Room room = RoomFactory.create(type, id, beds, floor);
        roomRepository.save(room);
    }

    public List<RoomSummaryDto> listRooms(RoomType typeFilter, Integer floorFilter) {
        return roomRepository.findAll().stream()
                .filter(r -> typeFilter == null || r.getType() == typeFilter)
                .filter(r -> floorFilter == null || r.getFloor() == floorFilter)
                .sorted(Comparator.comparingInt(Room::getId))
                .map(r -> new RoomSummaryDto(r.getId(), r.getBeds()))
                .collect(Collectors.toList());
    }


    public RoomDetailsDto getRoomDetails(int roomId) {
        Room r = roomRepository.findById(roomId).orElseThrow(() -> new NotFoundException("Room not found"));
        String desc = r.getType() + " room (" + r.getBeds() + " bed(s)) on floor " + r.getFloor();
        return new RoomDetailsDto(r.getId(), r.getBeds(), r.getFloor(), r.getType(), r.getStatus(), r.getVacateDate(), desc);
    }


    public void changeRoomStatus(User actor, int roomId, RoomStatus status) {
        AccessPolicy.requireAny(actor, Role.ADMIN_MAIN, Role.ADMIN_SERVICES);
        Room r = roomRepository.findById(roomId).orElseThrow(() -> new NotFoundException("Room not found"));
        r.setStatus(status);
        roomRepository.save(r);
    }
}
