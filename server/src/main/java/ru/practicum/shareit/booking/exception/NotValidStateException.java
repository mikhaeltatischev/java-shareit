package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotValidStateException extends RuntimeException {

    public NotValidStateException(String message) {
        super(message);
        log.info(message);
    }
}
