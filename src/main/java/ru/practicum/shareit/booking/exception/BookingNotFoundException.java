package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(Long id) {
        super(getMessage(id));
        log.info(getMessage(id));
    }

    private static String getMessage(Long id) {
        return "Booking with id: " + id + " not found";
    }
}
