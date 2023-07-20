package ru.practicum.shareit.request.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemRequestNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Item request with id: %d not found";

    public ItemRequestNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
        log.info(String.format(MESSAGE, id));
    }
}
