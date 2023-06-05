package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Set;

public interface UserService {

    User getUserById(Long id);

    Set<User> getUsers();

    User addUser(User user);

    User updateUser(User user);

    Long deleteUser(User user);
}
