package ru.practicum.shareit.request.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemRequestNotFoundException extends RuntimeException {

    public ItemRequestNotFoundException(String message) {
        super(message);
        log.info(message);
    }
}
