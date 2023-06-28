package ru.practicum.shareit.user.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super(getMessage(id));
        log.info(getMessage(id));
    }

    private static String getMessage(Long id) {
        return "User with id: " + id + " not found";
    }
}
