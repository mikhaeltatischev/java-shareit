package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class BookingTimeException extends RuntimeException{

    public BookingTimeException(LocalDateTime start, LocalDateTime end) {
        super(getMessage(start, end));
        log.info(getMessage(start, end));
    }

    private static String getMessage(LocalDateTime start, LocalDateTime end) {
        return "Start time: " + start + " must be before End time: " + end;
    }
}
