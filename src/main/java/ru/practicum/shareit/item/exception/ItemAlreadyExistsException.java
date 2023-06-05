package ru.practicum.shareit.item.exception;

public class ItemAlreadyExistsException extends RuntimeException {

    public ItemAlreadyExistsException(String message) {
        super(message);
    }
}
