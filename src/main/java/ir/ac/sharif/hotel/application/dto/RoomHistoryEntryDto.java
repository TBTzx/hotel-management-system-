package ir.ac.sharif.hotel.application.dto;

import ir.ac.sharif.hotel.domain.model.reservation.ReservationStatus;

import java.time.LocalDate;

public record RoomHistoryEntryDto(
        String reservationId,
        String username,
        int roomId,
        LocalDate startDate,
        LocalDate endDate,
        ReservationStatus status,
        boolean paid
) {}
