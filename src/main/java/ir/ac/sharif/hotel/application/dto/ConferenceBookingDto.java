package ir.ac.sharif.hotel.application.dto;

import java.time.LocalDateTime;


public record ConferenceBookingDto(
        String id,
        String reservationId,
        String username,
        LocalDateTime start,
        LocalDateTime end,
        long price,
        boolean upcoming
) {}
