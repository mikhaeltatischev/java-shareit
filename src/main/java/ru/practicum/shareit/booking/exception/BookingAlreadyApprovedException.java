package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingAlreadyApprovedException extends RuntimeException {

    public BookingAlreadyApprovedException(Long id) {
        super(getMessage(id));
        log.info(getMessage(id));
    }

    private static String getMessage(Long id) {
        return "Booking with id: " + id + " already approved";
    }
}
