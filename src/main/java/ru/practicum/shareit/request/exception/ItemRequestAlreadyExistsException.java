package ru.practicum.shareit.request.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemRequestAlreadyExistsException extends RuntimeException {

    public ItemRequestAlreadyExistsException(String message) {
        super(message);
        log.info(message);
    }
}
