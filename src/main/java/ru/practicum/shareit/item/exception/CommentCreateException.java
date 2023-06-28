package ru.practicum.shareit.item.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommentCreateException extends RuntimeException {

    public CommentCreateException(String message) {
        super(message);
        log.info(message);
    }
}
