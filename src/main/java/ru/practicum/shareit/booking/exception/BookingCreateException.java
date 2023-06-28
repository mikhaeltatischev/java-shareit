package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingCreateException extends RuntimeException {

    public BookingCreateException(Long userId, Long itemId) {
        super(getMessage(userId, itemId));
        log.info(getMessage(userId, itemId));
    }

    private static String getMessage(Long userId, Long itemId) {
        return "User with id: " + userId + " is the owner item with id: " + itemId;
    }
}
