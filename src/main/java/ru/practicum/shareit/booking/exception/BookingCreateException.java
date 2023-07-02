package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingCreateException extends RuntimeException {

    private static final String MESSAGE = "User with id: %d is the owner item with id: %d";

    public BookingCreateException(Long userId, Long itemId) {
        super(String.format(MESSAGE, userId, itemId));
        log.info(String.format(MESSAGE, userId, itemId));
    }
}
