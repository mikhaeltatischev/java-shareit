package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public Set<UserDto> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public UserDto addUser(User user) {
        return userRepository.addUser(user);
    }

    @Override
    public UserDto updateUser(User user, Long userId) {
        return userRepository.updateUser(user, userId);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
