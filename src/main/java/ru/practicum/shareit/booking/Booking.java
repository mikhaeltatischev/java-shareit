package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {

    private final Long id;
    private final String author;
    private LocalDateTime startBookingDate;
    private LocalDateTime endBookingDate;
}
