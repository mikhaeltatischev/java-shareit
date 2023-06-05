package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {

    private static Long userId = 1L;
    private Set<User> users = new HashSet<>();

    @Override
    public User getUserById(Long id) {
        User user = findUser(id);
        log.info("User with id: " + user);

        return user;
    }

    @Override
    public Set<User> getUsers() {
        return users;
    }

    @Override
    public User addUser(User user) {
        try {
            findUser(user.getId());
            user.setId(userId++);
            users.add(user);
            log.info("User with id: " + user.getId() + " saved");

            return user;
        } catch (UserNotFoundException e) {
            log.info("User with id: " + user.getId() + " already exists");
            throw new UserAlreadyExistsException("User with id: " + user.getId() + " already exists");
        }
    }

    @Override
    public User updateUser(User user) {
        try {
            findUser(user.getId());
            users.remove(user);
            users.add(user);

            return user;
        } catch (UserNotFoundException e) {
            log.info(e.getMessage());
            throw new UserNotFoundException("User with id: " + user.getId() + " not found");
        }
    }

    @Override
    public Long deleteUser(User user) {
        try {
            findUser(user.getId());
            users.remove(user);

            return user.getId();
        } catch (UserNotFoundException e) {
            log.info(e.getMessage());
            throw new UserNotFoundException("User with id: " + user.getId() + " not found");
        }
    }

    private User findUser(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }
}
