package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailDuplicateException;
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
    public UserDto getUserById(Long id) {
        User user = findUser(id);
        log.info("Found user: " + user);

        return createUserDto(user);
    }

    @Override
    public Set<UserDto> getUsers() {
        Set<UserDto> usersDto = new HashSet<>();

        users.stream().forEach(user -> usersDto.add(createUserDto(user)));

        return usersDto;
    }

    @Override
    public UserDto addUser(User user) {
        try {
            findUser(user.getId());
            log.info("User with id: " + user.getId() + " already exists");
            throw new UserAlreadyExistsException("User with id: " + user.getId() + " already exists");
        } catch (UserNotFoundException e) {
            checkDuplicateEmail(user, user.getId());
            user.setId(userId++);
            users.add(user);
            log.info("User with id: " + user.getId() + " saved");

            return createUserDto(user);
        }
    }

    @Override
    public UserDto updateUser(User user, Long userId) {
        try {
            User currentUser = findUser(userId);

            if (user.getName() != null) {
                currentUser.setName(user.getName());
            }

            if (user.getEmail() != null) {
                checkDuplicateEmail(user, userId);
                currentUser.setEmail(user.getEmail());
            }

            return createUserDto(currentUser);
        } catch (UserNotFoundException e) {
            log.info(e.getMessage());
            throw new UserNotFoundException("User with id: " + user.getId() + " not found");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            User user = findUser(userId);
            users.remove(user);
            log.info("User with id: " + userId + " removed");
        } catch (UserNotFoundException e) {
            log.info(e.getMessage());
            throw new UserNotFoundException("User with id: " + userId + " not found");
        }
    }

    private User findUser(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }

    private UserDto createUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    private void checkDuplicateEmail(User user, Long userId) {
        users.stream()
                .filter(currentUser -> currentUser.getEmail().equals(user.getEmail()))
                .forEach(currentUser -> {
                    if (!currentUser.getId().equals(userId)) {
                        throw new EmailDuplicateException("User with this email already exists");
                    }
                });
    }
}
