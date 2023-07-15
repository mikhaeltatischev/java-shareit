package ru.practicum.shareit.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldIsNotValidException extends RuntimeException {

    private static final String MESSAGE = "Field of \"%s\" is not valid";

    public FieldIsNotValidException(String field) {
        super(String.format(MESSAGE, field));
        log.info(String.format(MESSAGE, field));
    }
}
