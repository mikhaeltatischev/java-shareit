package ru.practicum.shareit.request.exception;

public class ItemRequestNotFoundException extends RuntimeException {

    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}
