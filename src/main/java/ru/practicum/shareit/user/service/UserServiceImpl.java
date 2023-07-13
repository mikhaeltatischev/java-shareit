package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.FieldIsNotValidException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.user.dto.UserDtoMapper.toDto;
import static ru.practicum.shareit.user.dto.UserDtoMapper.toUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        log.info("Found User: " + user);

        return toDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();

        log.info("Found users: " + users);

        return toDto(users);
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto user) {
        User savedUser = userRepository.save(toUser(user));

        log.info("User with id: " + savedUser.getUserId() + " saved");

        return toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        update(user, userDto);
        userRepository.save(user);
        log.info("User with id: " + userId + " updated");

        return toDto(user);
    }

    @Override
    @Transactional
    public UserDto deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.deleteById(userId);
        log.info("User with id: " + userId + " deleted");

        return toDto(user);
    }

    private User update(User user, UserDto userDto) {
        if (userDto.getEmail() != null) {
            if (userDto.getEmail().isBlank() || !userDto.getEmail().matches(".+[@].+[\\.].+")) {
                throw new FieldIsNotValidException("Email");
            } else {
                user.setEmail(userDto.getEmail());
            }
        }

        if (userDto.getName() != null) {
            if (userDto.getName().isBlank()) {
                throw new FieldIsNotValidException("Name");
            } else {
                user.setName(userDto.getName());
            }
        }

        return user;
    }
}