package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.Review;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

public interface UserService {

    UserDto getUserById(Long id);

    Set<UserDto> getUsers();

    UserDto addUser(User user);

    UserDto updateUser(User user, Long userId);

    void deleteUser(Long userId);

    UserDto addReviewToUser(Review review, Long userId, Long sharedUser);
}
