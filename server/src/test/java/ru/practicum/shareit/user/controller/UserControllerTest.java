package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto exceptedUser;
    private long userId;

    @BeforeEach
    public void fillUsers() {
        userId = 1L;
        exceptedUser = new UserDto(userId, "Jon Bon", "mail@mail.ru");
    }

    @Test
    @SneakyThrows
    public void getUserByIdWhenUserNotFoundThrowException() {
        when(userService.getUserById(userId)).thenThrow(UserNotFoundException.class);

        mvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).getUserById(userId);
    }

    @Test
    @SneakyThrows
    public void getUserByIdWhenUserFoundReturnDefaultUser() {
        mvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getUserById(userId);
    }

    @Test
    @SneakyThrows
    public void getUsersWhenOneUserFoundReturnListWithOneUser() {
        List<UserDto> users = List.of(exceptedUser);
        when(userService.getUsers()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));

        verify(userService).getUsers();
    }

    @Test
    @SneakyThrows
    public void getUsersWhenUsersNotFoundReturnEmptyList() {
        List<UserDto> users = List.of();
        when(userService.getUsers()).thenReturn(users);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));

        verify(userService).getUsers();
    }

    @Test
    @SneakyThrows
    public void addUserWhenUserIsValidReturnUser() {
        when(userService.addUser(exceptedUser)).thenReturn(exceptedUser);

        mvc.perform(post("/users")
                    .content(mapper.writeValueAsString(exceptedUser))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.name", is("Jon Bon")))
                .andExpect(jsonPath("$.email", is("mail@mail.ru")));

        verify(userService).addUser(exceptedUser);
    }


    @Test
    @SneakyThrows
    public void updateUserWhenUserIsValidReturnUpdatedUser() {
        UserDto updateUser = new UserDto(userId, "Update jon", "update@mail.com");
        when(userService.updateUser(updateUser, userId)).thenReturn(updateUser);

        mvc.perform(patch("/users/{userId}", userId)
                    .content(mapper.writeValueAsString(updateUser))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.name", is("Update jon")))
                .andExpect(jsonPath("$.email", is("update@mail.com")));

    }

    @Test
    @SneakyThrows
    public void deleteUserWhenUserDeletedReturnUser() {
        when(userService.deleteUser(userId)).thenReturn(exceptedUser);

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.name", is("Jon Bon")))
                .andExpect(jsonPath("$.email", is("mail@mail.ru")));

        verify(userService).deleteUser(userId);
    }

    @Test
    @SneakyThrows
    public void deleteUserWhenUserNotFoundThrowException() {
        when(userService.deleteUser(userId)).thenThrow(UserNotFoundException.class);

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }
}