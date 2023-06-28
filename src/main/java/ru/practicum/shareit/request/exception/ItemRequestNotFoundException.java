package ru.practicum.shareit.request.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemRequestNotFoundException extends RuntimeException {

    public ItemRequestNotFoundException(Long id) {
        super(getMessage(id));
        log.info(getMessage(id));
    }

    private static String getMessage(Long id) {
        return "Item request with id: " + id + " not found";
    }
}
