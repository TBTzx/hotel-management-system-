package ir.ac.sharif.hotel.application.dto;

import ir.ac.sharif.hotel.domain.model.room.RoomStatus;
import ir.ac.sharif.hotel.domain.model.room.RoomType;

import java.time.LocalDate;

public record RoomDetailsDto(
        int id,
        int beds,
        int floor,
        RoomType type,
        RoomStatus status,
        LocalDate vacateDate,
        String description
) {}
