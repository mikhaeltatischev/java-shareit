package ru.practicum.shareit.user.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailDuplicateException extends RuntimeException {

    public EmailDuplicateException(String message) {
        super(message);
        log.info(message);
    }
}
