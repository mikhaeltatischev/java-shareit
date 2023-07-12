package ru.practicum.shareit.item.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotAvailableException extends RuntimeException {

    private static final String MESSAGE = "Item with id: %d is not available now";

    public ItemNotAvailableException(Long id) {
        super(String.format(MESSAGE, id));
        log.info(String.format(MESSAGE, id));
    }
}
