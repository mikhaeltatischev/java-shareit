package ru.practicum.shareit.request.exception;

public class ItemRequestAlreadyExistsException extends RuntimeException {

    public ItemRequestAlreadyExistsException(String message) {
        super(message);
    }
}
