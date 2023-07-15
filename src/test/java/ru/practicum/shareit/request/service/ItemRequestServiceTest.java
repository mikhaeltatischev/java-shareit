package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.common.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.GetItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.request.dto.ItemRequestDtoMapper.toDto;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private long requestId;
    private long authorId;
    private long itemId;
    private long ownerItemId;
    private User user;
    private Item item;
    private ItemRequest itemRequest;
    private User author;

    @BeforeEach
    public void setUp() {
        requestId = 1L;
        authorId = 1L;
        itemId = 1L;
        ownerItemId = 2L;

        user = User.builder()
                .userId(ownerItemId)
                .name("Jon Bon")
                .email("mail@mail.com")
                .build();

        author = User.builder()
                .userId(authorId)
                .name("Bon Jon")
                .email("google@google.com")
                .build();

        item = Item.builder()
                .itemId(itemId)
                .user(user)
                .available(true)
                .name("Отвертка")
                .description("Крутая отвертка")
                .requestId(1L)
                .build();

        itemRequest = ItemRequest.builder()
                .itemRequestId(requestId)
                .timeOfCreation(LocalDateTime.now())
                .user(author)
                .description("отвертка")
                .build();
    }

    @Test
    public void addItemRequestWhenInvokedMethodReturnRequest() {
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(repository.save(itemRequest)).thenReturn(itemRequest);

        assertEquals(toDto(itemRequest), service.addItemRequest(toDto(itemRequest), authorId));
    }

    @Test
    public void addItemRequestWhenUserNotFoundThrowException() {
        when(userRepository.findById(authorId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> service.addItemRequest(toDto(itemRequest), authorId));
    }

    @Test
    public void getItemRequestsForUserWhenInvokedMethodReturnOneRequest() {
        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(repository.getItemRequestsByUserUserId(authorId)).thenReturn(itemRequests);
        when(itemRepository.findAllByRequests(any())).thenReturn(List.of(item));

        assertEquals(toDto(itemRequests), service.getItemRequestsForUser(authorId));
    }

    @Test
    public void getItemRequestsForUserWhenUserNotFoundThrowException() {
        when(userRepository.findById(authorId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> service.getItemRequestsForUser(authorId));
    }

    @Test
    public void getItemRequestsForUserWhenRequestsNotFoundReturnEmptyList() {
        List<ItemRequest> itemRequests = List.of();

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(repository.getItemRequestsByUserUserId(authorId)).thenReturn(itemRequests);
        when(itemRepository.findAllByRequests(any())).thenReturn(List.of(item));

        assertEquals(0, service.getItemRequestsForUser(authorId).size());
    }

    @Test
    public void getItemRequestByIdWhenInvokedMethodReturnBooking() {
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(repository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(any())).thenReturn(List.of(item));

        assertEquals(toDto(itemRequest), service.getItemRequestById(requestId, authorId));
    }

    @Test
    public void getItemRequestByIdWhenRequestNotFoundThrowException() {
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(repository.findById(requestId)).thenThrow(ItemRequestNotFoundException.class);

        assertThrows(ItemRequestNotFoundException.class, () -> service.getItemRequestById(requestId, authorId));
    }

    @Test
    public void getItemRequestByIdWhenUserNotFoundThrowException() {
        when(userRepository.findById(authorId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> service.getItemRequestById(requestId, authorId));
    }

    @Test
    public void deleteItemRequestWhenInvokedMethodReturnRequest() {
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(repository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        assertEquals(toDto(itemRequest), service.deleteItemRequest(requestId, authorId));
    }

    @Test
    public void deleteItemRequestWhenUserDoesntHaveAccessThrowException() {
        when(userRepository.findById(ownerItemId)).thenReturn(Optional.of(user));
        when(repository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        assertThrows(NotOwnerException.class, () -> service.deleteItemRequest(requestId, ownerItemId));
    }

    @Test
    public void getItemRequestsWhenInvokedMethodReturnOneBooking() {
        List<ItemRequest> itemRequests = List.of(itemRequest);

        when(userRepository.findById(ownerItemId)).thenReturn(Optional.of(user));
        when(repository.findAllByUserUserIdNotLike(ownerItemId, PageRequest.of(0, 10))).thenReturn(itemRequests);
        when(itemRepository.findAllByRequests(any())).thenReturn(List.of(item));

        assertEquals(toDto(itemRequests), service.getItemRequests(GetItemRequest.of(ownerItemId, 0, 10)));
    }

    @Test
    public void getItemRequestsWhenInvokedMethodReturnEmptyList() {
        List<ItemRequest> itemRequests = List.of();

        when(userRepository.findById(ownerItemId)).thenReturn(Optional.of(user));
        when(repository.findAllByUserUserIdNotLike(ownerItemId, PageRequest.of(0, 10))).thenReturn(itemRequests);
        when(itemRepository.findAllByRequests(any())).thenReturn(List.of(item));

        assertEquals(toDto(itemRequests), service.getItemRequests(GetItemRequest.of(ownerItemId, 0, 10)));
    }
}