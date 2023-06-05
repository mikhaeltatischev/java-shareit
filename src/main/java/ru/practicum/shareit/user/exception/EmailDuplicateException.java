package ru.practicum.shareit.user.exception;

public class EmailDuplicateException extends RuntimeException {

    public EmailDuplicateException(String message) {
        super(message);
    }
}
