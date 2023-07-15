package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.dto.UserDtoMapper.toDto;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    @Captor
    private ArgumentCaptor<User> argumentCaptor;

    private User exceptedUser;
    private long userId;

    @BeforeEach
    public void fillUsers() {
        userId = 1L;
        exceptedUser = new User(userId, "Jon Bon", "mail@mail.ru");
    }

    @Test
    public void getUserByIdWhenUserFoundReturnUser() {
        when(repository.findById(userId)).thenReturn(Optional.of(exceptedUser));

        UserDto foundUser = service.getUserById(userId);

        assertEquals(toDto(exceptedUser), foundUser);
    }

    @Test
    public void getUserByIdWhenUserNotFoundThrowException() {
        long userId = 0L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getUserById(userId));
    }

    @Test
    public void getUsersWhenOneUserFoundReturnListWithOneUser() {
        List<User> users = List.of(exceptedUser);
        when(repository.findAll()).thenReturn(users);

        List<UserDto> foundUsers = service.getUsers();

        assertEquals(toDto(users), foundUsers);
        assertEquals(1, foundUsers.size());
    }

    @Test
    public void getUsersWhenUsersNotFoundReturnEmptyList() {
        List<User> users = List.of();
        when(repository.findAll()).thenReturn(users);

        List<UserDto> foundUsers = service.getUsers();

        assertEquals(toDto(users), service.getUsers());
        assertEquals(0, foundUsers.size());
    }

    @Test
    public void addUserWhenUserAddReturnUser() {
        when(repository.save(exceptedUser)).thenReturn(exceptedUser);

        assertEquals(toDto(exceptedUser), service.addUser(toDto(exceptedUser)));
        verify(repository).save(exceptedUser);
    }

    @Test
    public void updateUserWhenUserUpdateReturnUpdatedUser() {
        User newUser = new User();
        newUser.setEmail("newMail@mail.ru");
        newUser.setName("newName Jon");

        when(repository.findById(userId)).thenReturn(Optional.of(exceptedUser));

        service.updateUser(toDto(newUser), userId);

        verify(repository).save(argumentCaptor.capture());
        User updatedUser = argumentCaptor.getValue();

        assertEquals("newMail@mail.ru", updatedUser.getEmail());
        assertEquals("newName Jon", updatedUser.getName());
    }

    @Test
    public void updateUserWhenUserNotFoundThrowException() {
        long userId = 0L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.updateUser(toDto(exceptedUser), userId));
    }

    @Test
    public void deleteUserWhenUserDeletedDontThrowException() {
        when(repository.findById(userId)).thenReturn(Optional.of(exceptedUser));

        assertEquals(toDto(exceptedUser), service.deleteUser(userId));
        verify(repository).deleteById(userId);
    }

    @Test
    public void deleteUserWhenUserNotFoundThrowException() {
        long userId = 0L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.deleteUser(userId));
        verify(repository, never()).deleteById(userId);
    }
}
