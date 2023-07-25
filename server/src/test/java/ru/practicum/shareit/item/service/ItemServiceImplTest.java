package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repostitory.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CommentCreateException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.common.NotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.RequestItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toDto;
import static ru.practicum.shareit.item.dto.CommentDtoMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentDtoMapper.toDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.toItem;
import static ru.practicum.shareit.user.dto.UserDtoMapper.toUser;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl service;

    private ItemBookingDto itemBooking;
    private UserDto userDto;
    private ItemDto itemDto;
    private long itemId;
    private long userId;

    @BeforeEach
    public void setUp() {
        itemId = 1L;
        userId = 1L;
        userDto = new UserDto(userId, "Jon Bon", "mail@mail.ru");
        itemBooking = new ItemBookingDto(itemId, "Отвертка", userDto, "Классная отвертка", true);
        itemBooking.setComments(new ArrayList<>());
        itemDto = new ItemDto(itemId, "Стремянка", userDto, "Высокая стремянка", true, 1L);
    }

    @Test
    public void getItemByIdWhenItemFoundReturnItem() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, toUser(userDto))));

        ItemBookingDto foundItem = service.getItemById(itemId, userId);

        assertEquals(itemBooking, foundItem);
        verify(bookingRepository).findAllByItemItemId(itemId);
        verify(commentRepository).findAllCommentByItemItemId(itemId);
    }

    @Test
    public void getItemByIdWhenUserIsOwnerTheItemReturnItemWithBookings() {
        Item item = toItem(itemBooking, toUser(userDto));
        User currentUser = toUser(userDto);

        Booking lastBooking = new Booking(1L, currentUser, item, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), Status.APPROVED);
        Booking nextBooking = new Booking(2L, currentUser, item, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.APPROVED);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, toUser(userDto))));
        when(bookingRepository.findAllByItemItemId(itemId)).thenReturn(List.of(lastBooking, nextBooking));

        ItemBookingDto foundItem = service.getItemById(itemId, userId);
        itemBooking.setLastBooking(toDto(lastBooking));
        itemBooking.setNextBooking(toDto(nextBooking));

        assertEquals(itemBooking, foundItem);
        verify(bookingRepository).findAllByItemItemId(itemId);
        verify(commentRepository).findAllCommentByItemItemId(itemId);
    }

    @Test
    public void getItemByIdWhenItemNotExistsThrowException() {
        when(itemRepository.findById(itemId)).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> service.getItemById(itemId, userId));
    }

    @Test
    public void getItemByIdWhenItemHasBookingsAndCommentsReturnItemWithBookingsAndComments() {
        Item item = toItem(itemBooking, toUser(userDto));
        User currentUser = toUser(userDto);

        Booking lastBooking = new Booking(1L, currentUser, item, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), Status.APPROVED);
        Booking nextBooking = new Booking(2L, currentUser, item, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.APPROVED);
        Comment comment = new Comment(1L, currentUser, 1, "text", LocalDateTime.now().minusDays(1), item);
        List<Booking> bookings = List.of(lastBooking, nextBooking);
        List<Comment> comments = List.of(comment);

        itemBooking.setLastBooking(toDto(lastBooking));
        itemBooking.setNextBooking(toDto(nextBooking));
        itemBooking.setComments(toDto(comments));

        when(bookingRepository.findAllByItemItemId(itemId)).thenReturn(bookings);
        when(commentRepository.findAllCommentByItemItemId(itemId)).thenReturn(comments);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, currentUser)));

        assertEquals(itemBooking, service.getItemById(itemId, userId));
    }

    @Test
    public void getItemByIdWhenItemHasOneBookingReturnWithBookingAndComments() {
        Item item = toItem(itemBooking, toUser(userDto));
        User currentUser = toUser(userDto);

        Booking lastBooking = new Booking(1L, currentUser, item, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), Status.APPROVED);

        Comment comment = new Comment(1L, currentUser, 1, "text", LocalDateTime.now().minusDays(1), item);
        List<Booking> bookings = List.of(lastBooking);
        List<Comment> comments = List.of(comment);

        itemBooking.setLastBooking(toDto(lastBooking));
        itemBooking.setComments(toDto(comments));

        when(bookingRepository.findAllByItemItemId(itemId)).thenReturn(bookings);
        when(commentRepository.findAllCommentByItemItemId(itemId)).thenReturn(comments);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, currentUser)));

        assertEquals(itemBooking, service.getItemById(itemId, userId));
    }

    @Test
    public void getItemByIdWhenItemHasBookingsReturnItemWithBookings() {
        Item item = toItem(itemBooking, toUser(userDto));
        User currentUser = toUser(userDto);

        Booking lastBooking = new Booking(1L, currentUser, item, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), Status.APPROVED);
        Booking nextBooking = new Booking(2L, currentUser, item, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.APPROVED);
        List<Booking> bookings = List.of(lastBooking, nextBooking);

        itemBooking.setLastBooking(toDto(lastBooking));
        itemBooking.setNextBooking(toDto(nextBooking));

        when(bookingRepository.findAllByItemItemId(itemId)).thenReturn(bookings);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, currentUser)));

        assertEquals(itemBooking, service.getItemById(itemId, userId));
    }

    @Test
    public void getItemByIdWhenItemHasCommentsReturnItemWithComments() {
        Item item = toItem(itemBooking, toUser(userDto));
        User currentUser = toUser(userDto);

        Comment comment = new Comment(1L, currentUser, 1, "text", LocalDateTime.now().minusDays(1), item);
        List<Comment> comments = List.of(comment);

        itemBooking.setComments(toDto(comments));

        when(commentRepository.findAllCommentByItemItemId(itemId)).thenReturn(comments);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, currentUser)));

        assertEquals(itemBooking, service.getItemById(itemId, userId));
    }

    @Test
    public void getItemsForUserWhenFoundOneItemReturnItem() {
        Item item = toItem(itemBooking, toUser(userDto));
        User currentUser = toUser(userDto);
        RequestItem requestItem = RequestItem.of(1L, 0, 10, "text");
        PageRequest pageRequest = PageRequest.of(requestItem.getFrom(), requestItem.getSize());
        Booking lastBooking = new Booking(1L, currentUser, item, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), Status.APPROVED);
        Booking nextBooking = new Booking(2L, currentUser, item, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.APPROVED);
        Comment comment = new Comment(1L, currentUser, 1, "text", LocalDateTime.now().minusDays(1), item);

        List<Booking> bookings = List.of(lastBooking, nextBooking);
        List<Comment> comments = List.of(comment);

        itemBooking.setLastBooking(toDto(lastBooking));
        itemBooking.setNextBooking(toDto(nextBooking));
        itemBooking.setComments(toDto(comments));

        List<Item> items = List.of(toItem(itemBooking, toUser(userDto)));
        List<ItemBookingDto> itemsDto = List.of(itemBooking);

        when(bookingRepository.findAllByItems(items)).thenReturn(bookings);
        when(commentRepository.findAllByItems(items)).thenReturn(comments);
        when(itemRepository.findAllByUserUserId(userId, pageRequest)).thenReturn(items);

        List<ItemBookingDto> foundItems = service.getItemsForUser(requestItem);

        assertEquals(itemsDto, foundItems);
        assertEquals(1, foundItems.size());
    }

    @Test
    public void getItemsForUserWhenItemNotFoundReturnEmptyList() {
        List<ItemBookingDto> items = List.of();
        RequestItem requestItem = RequestItem.of(1L, 0, 10, "text");

        List<ItemBookingDto> foundItems = service.getItemsForUser(requestItem);

        assertEquals(items, foundItems);
        assertEquals(0, foundItems.size());
    }

    @Test
    public void updateItemWhenSetNewNameReturnUpdatedItem() {
        ItemDto newItem = new ItemDto();
        newItem.setName("name");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));

        ItemDto updatedItem = service.updateItem(newItem, userId, itemId);

        assertEquals(newItem, updatedItem);
        assertEquals(newItem.getName(), updatedItem.getName());
        verify(itemRepository).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenSetNewDescriptionReturnUpdatedItem() {
        ItemDto newItem = new ItemDto();
        newItem.setDescription("description");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));

        ItemDto updatedItem = service.updateItem(newItem, userId, itemId);

        assertEquals(newItem, updatedItem);
        assertEquals(newItem.getDescription(), updatedItem.getDescription());
        verify(itemRepository).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenSetNewAvailableReturnUpdatedItem() {
        ItemDto newItem = new ItemDto();
        newItem.setAvailable(false);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));

        ItemDto updatedItem = service.updateItem(newItem, userId, itemId);

        assertEquals(newItem, updatedItem);
        assertEquals(newItem.getAvailable(), updatedItem.getAvailable());
        verify(itemRepository).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenUserNotFoundThrowException() {
        long userId = 0L;
        ItemDto newItem = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.updateItem(newItem, userId, itemId));
        verify(itemRepository, never()).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenItemNotFoundThrowException() {
        long itemId = 0L;
        ItemDto newItem = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.updateItem(newItem, userId, itemId));
        verify(itemRepository, never()).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenUserIsNotOwnerTheItemThrowException() {
        long userId = 2L;
        User newUser = new User();
        newUser.setUserId(userId);
        ItemDto newItem = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBooking, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(newUser));

        assertThrows(NotOwnerException.class, () -> service.updateItem(newItem, userId, itemId));
        verify(itemRepository, never()).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void addItemWhenMethodInvokedReturnItem() {
        Item currentItem = toItem(itemDto, toUser(userDto));

        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));
        when(itemRepository.save(currentItem)).thenReturn(currentItem);

        assertEquals(itemDto, service.addItem(itemDto, userId));
        verify(itemRepository).save(currentItem);
    }

    @Test
    public void addItemWhenUserNotFoundReturnItem() {
        long userId = 0L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.addItem(itemDto, userId));
        verify(itemRepository, never()).save(any());
    }

    @Test
    public void deleteItemWhenMethodInvokedReturnDeletedItem() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemDto, toUser(userDto))));

        assertEquals(itemDto, service.deleteItem(itemId, userId));
        verify(itemRepository).delete(any());
    }

    @Test
    public void deleteItemWhenUserIsNotOwnerTheItemThrowException() {
        long userId = 2L;
        User newUser = new User();
        newUser.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(newUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemDto, toUser(userDto))));

        assertThrows(NotOwnerException.class, () -> service.deleteItem(itemId, userId));
        verify(itemRepository, never()).delete(any());
    }

    @Test
    public void deleteItemWhenUserNotFoundThrowException() {
        long userId = 0L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.deleteItem(itemId, userId));
        verify(itemRepository, never()).delete(any());
    }

    @Test
    public void deleteItemWhenItemNotFoundThrowException() {
        long userId = 0L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.deleteItem(itemId, userId));
        verify(itemRepository, never()).delete(any());
    }

    @Test
    public void searchItemWhenInvokedMethodReturnOneItem() {
        RequestItem requestItem = RequestItem.of(1L, 0, 10, "text");
        PageRequest pageRequest = PageRequest.of(requestItem.getFrom(), requestItem.getSize());
        List<ItemDto> items = List.of(itemDto);

        when(itemRepository.findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                requestItem.getText(), requestItem.getText(), pageRequest)).thenReturn(List.of(toItem(itemDto, toUser(userDto))));

        assertEquals(items, service.searchItem(requestItem));
    }

    @Test
    public void searchItemWhenNotFoundItemsReturnEmptyList() {
        RequestItem requestItem = RequestItem.of(1L, 0, 10, "text");
        PageRequest pageRequest = PageRequest.of(requestItem.getFrom(), requestItem.getSize());
        List<ItemDto> items = List.of();

        when(itemRepository.findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                requestItem.getText(), requestItem.getText(), pageRequest)).thenReturn(List.of());

        List<ItemDto> foundItems = service.searchItem(requestItem);

        assertEquals(items, foundItems);
        assertEquals(0, foundItems.size());
    }

    @Test
    public void searchItemWhenRequestTextIsBlankReturnEmptyList() {
        RequestItem requestItem = RequestItem.of(1L, 0, 10, "");
        List<ItemDto> items = List.of();

        List<ItemDto> foundItems = service.searchItem(requestItem);

        assertEquals(items, foundItems);
        assertEquals(0, foundItems.size());
    }

    @Test
    public void addCommentWhenMethodInvokeReturnComment() {
        long userId = 2L;
        Item item = toItem(itemDto, toUser(userDto));
        userDto.setId(userId);
        User user = toUser(userDto);
        CommentDto commentDto = new CommentDto(1L, userDto, userDto.getName(), userId, 1, "text",
                LocalDateTime.now(), itemDto, itemId);
        Comment comment = toComment(commentDto, user, item);
        Booking lastBooking = new Booking(1L, user, item, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), Status.APPROVED);
        List<Booking> bookings = List.of(lastBooking);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(anyLong(), any()))
                .thenReturn(bookings);
        when(commentRepository.save(any())).thenReturn(comment);

        assertEquals(commentDto, service.addComment(commentDto, userId, itemId));
    }

    @Test
    public void addCommentWhenUserIsOwnerOfTheItemThrowException() {
        User user = toUser(userDto);
        Item item = toItem(itemDto, user);
        CommentDto commentDto = new CommentDto(1L, userDto, userDto.getName(), userId, 1, "text",
                LocalDateTime.now(), itemDto, itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(CommentCreateException.class, () -> service.addComment(commentDto, userId, itemId));
    }

    @Test
    public void addCommentWhenUserDidNotRentTheItemThrowException() {
        Long userId = 2L;
        Item item = toItem(itemDto, toUser(userDto));
        userDto.setId(userId);
        User user = toUser(userDto);
        CommentDto commentDto = new CommentDto(1L, userDto, userDto.getName(), userId, 1, "text",
                LocalDateTime.now(), itemDto, itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(CommentCreateException.class, () -> service.addComment(commentDto, userId, itemId));
    }
}