package ru.practicum.shareit.item.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotAvailableException extends RuntimeException {

    public ItemNotAvailableException(Long id) {
        super(getMessage(id));
        log.info(getMessage(id));
    }

    private static String getMessage(Long id) {
        return "Item with id: " + id + " not available now";
    }
}
