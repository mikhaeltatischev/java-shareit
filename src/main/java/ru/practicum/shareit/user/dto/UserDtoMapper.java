package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDtoMapper {

    public static UserDto toDto(User user) {
        Long userId = user.getUserId();
        String name = user.getName();
        String email = user.getEmail();

        return new UserDto(userId, name, email);
    }

    public static User toUser(UserDto userDto) {
        Long userId = userDto.getId();
        String name = userDto.getName();
        String email = userDto.getEmail();

        return new User(userId, name, email);
    }

    public static List<UserDto> toDto(List<User> users) {
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users) {
            userDtos.add(toDto(user));
        }

        return userDtos;
    }
}
