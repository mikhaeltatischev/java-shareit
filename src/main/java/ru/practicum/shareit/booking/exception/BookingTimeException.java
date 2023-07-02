package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class BookingTimeException extends RuntimeException {

    private static final String MESSAGE = "Start time: %s must be before End time: %s";

    public BookingTimeException(LocalDateTime start, LocalDateTime end) {
        super(String.format(MESSAGE, start.toString(), end.toString()));
        log.info(String.format(MESSAGE, start, end));
    }
}
