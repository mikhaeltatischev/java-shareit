package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.RequestBooking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repostitory.BookingRepository;
import ru.practicum.shareit.common.NotOwnerException;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.dto.BookingDtoMapper.toDto;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private long userId;
    private long itemId;
    private long ownerId;
    private long bookingId;
    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private RequestBooking requestBooking;
    private LocalDateTime now;

    @BeforeEach
    public void setUp() {
        now = LocalDateTime.now();
        userId = 1L;
        itemId = 1L;
        bookingId = 1L;
        ownerId = 2L;

        owner = User.builder()
                .userId(ownerId)
                .email("google@mail.ru")
                .name("Bon Jon")
                .build();

        user = User.builder()
                .userId(userId)
                .email("mail@mail.ru")
                .name("Jon Bon")
                .build();

        item = Item.builder()
                .itemId(itemId)
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true)
                .user(owner)
                .build();

        booking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(user)
                .status(Status.APPROVED)
                .endTime(now.plusDays(1))
                .startTime(now.plusHours(2))
                .build();

        requestBooking = requestBooking.builder()
                .from(0)
                .size(10)
                .userId(userId)
                .build();
    }

    @Test
    public void addBookingWhenInvokedMethodReturnBooking() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        assertEquals(toDto(booking), bookingService.addBooking(toDto(booking), userId));
    }

    @Test
    public void addBookingWhenBookerIsOwnerTheItemThrowException() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BookingCreateException.class, () -> bookingService.addBooking(toDto(booking), ownerId));
    }

    @Test
    public void addBookingWhenItemIsNotAvailableThrowException() {
        item.setAvailable(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ItemNotAvailableException.class, () -> bookingService.addBooking(toDto(booking), userId));
    }

    @Test
    public void addBookingWhenStartTimeIsBeforeThanEndTimeThrowException() {
        booking.setStartTime(LocalDateTime.now().plusHours(2));
        booking.setEndTime(LocalDateTime.now().plusHours(1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(BookingTimeException.class, () -> bookingService.addBooking(toDto(booking), userId));
    }

    @Test
    public void deleteBookingWhenInvokedMethodReturnBooking() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertEquals(toDto(booking), bookingService.deleteBooking(bookingId, userId));
    }

    @Test
    public void deleteBookingWhenUserIsNotTheOwnerTheBookingThrowException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotOwnerException.class, () -> bookingService.deleteBooking(bookingId, ownerId));
    }

    @Test
    public void deleteBookingWhenBookingNotFoundThrowException() {
        when(bookingRepository.findById(bookingId)).thenThrow(BookingNotFoundException.class);

        assertThrows(BookingNotFoundException.class, () -> bookingService.deleteBooking(bookingId, userId));
    }

    @Test
    public void getBookingByIdWhenBookingNotFoundThrowException() {
        when(bookingRepository.findById(bookingId)).thenThrow(BookingNotFoundException.class);

        assertThrows(BookingNotFoundException.class, () -> bookingService.deleteBooking(bookingId, userId));
    }

    @Test
    public void getBookingByIdWhenInvokedMethodReturnBooking() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertEquals(toDto(booking), bookingService.getBookingById(userId, bookingId));
    }

    @Test
    public void setApproveWhenBookingAlreadyApprovedThrowException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingAlreadyApprovedException.class, () -> bookingService.setApprove(ownerId, bookingId, true));
    }

    @Test
    public void setApproveWhenUserNotTheOwnerTheItemThrowException() {
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotOwnerException.class, () -> bookingService.setApprove(userId, bookingId, true));
    }

    @Test
    public void setApproveWhenInvokedMethodReturnBooking() {
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto foundBooking = bookingService.setApprove(ownerId, bookingId, true);
        booking.setStatus(Status.APPROVED);

        assertEquals(toDto(booking), foundBooking);
    }

    @Test
    public void setApproveWhenBookingNotFoundThrowException() {
        when(bookingRepository.findById(bookingId)).thenThrow(BookingNotFoundException.class);

        assertThrows(BookingNotFoundException.class, () -> bookingService.setApprove(ownerId, bookingId, true));
    }

    @Test
    public void setApproveWhenApprovedFalseReturnBooking() {
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto returnedBooking = bookingService.setApprove(ownerId, bookingId, false);

        assertEquals(toDto(booking), returnedBooking);
        assertEquals(Status.REJECTED, returnedBooking.getStatus());
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsAllReturnOneBooking() {
        List<Booking> bookings = List.of(booking);
        List<BookingDto> foundBookings = List.of(toDto(booking));
        requestBooking.setState("ALL");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByUserUserIdOrderByEndTimeDesc(any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsCurrentReturnEmptyList() {
        List<Booking> bookings = List.of(booking);
        List<BookingDto> foundBookings = List.of(toDto(booking));
        requestBooking.setState("CURRENT");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(any(),
                any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsPastReturnOneBooking() {
        Booking pastBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(user)
                .status(Status.APPROVED)
                .endTime(now.minusDays(2))
                .startTime(now.minusDays(1))
                .build();

        List<Booking> bookings = List.of(pastBooking);
        List<BookingDto> foundBookings = List.of(toDto(pastBooking));
        requestBooking.setState("Past");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsFutureReturnOneBooking() {
        Booking futureBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(user)
                .status(Status.APPROVED)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(futureBooking);
        List<BookingDto> foundBookings = List.of(toDto(futureBooking));
        requestBooking.setState("Future");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsWaitingReturnOneBooking() {
        Booking waitingBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(user)
                .status(Status.WAITING)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(waitingBooking);
        List<BookingDto> foundBookings = List.of(toDto(waitingBooking));
        requestBooking.setState("Waiting");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(any(), any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsRejectedReturnOneBooking() {
        Booking rejectedBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(user)
                .status(Status.REJECTED)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(rejectedBooking);
        List<BookingDto> foundBookings = List.of(toDto(rejectedBooking));
        requestBooking.setState("REJECTED");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByUserUserIdAndStatusOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsNotValidReturnOneBooking() {
        requestBooking.setState("UNKNOWN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(NotValidStateException.class, () -> bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsAllReturnOneBooking() {
        List<Booking> bookings = List.of(booking);
        List<BookingDto> foundBookings = List.of(toDto(booking));
        requestBooking.setState("ALL");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllBookingByOwnerId(any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsCurrentReturnOneBooking() {
        List<Booking> bookings = List.of(booking);
        List<BookingDto> foundBookings = List.of(toDto(booking));
        requestBooking.setState("CURRENT");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(any(),
                any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsPastReturnOneBooking() {
        Booking pastBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(user)
                .status(Status.APPROVED)
                .endTime(now.minusDays(2))
                .startTime(now.minusDays(1))
                .build();

        List<Booking> bookings = List.of(pastBooking);
        List<BookingDto> foundBookings = List.of(toDto(pastBooking));
        requestBooking.setState("Past");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsFutureReturnOneBooking() {
        Booking futureBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(user)
                .status(Status.APPROVED)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(futureBooking);
        List<BookingDto> foundBookings = List.of(toDto(futureBooking));
        requestBooking.setState("Future");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsWaitingReturnOneBooking() {
        Booking waitingBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(user)
                .status(Status.WAITING)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(waitingBooking);
        List<BookingDto> foundBookings = List.of(toDto(waitingBooking));
        requestBooking.setState("Waiting");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(any(), any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsRejectedReturnOneBooking() {
        Booking rejectedBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(user)
                .status(Status.REJECTED)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(rejectedBooking);
        List<BookingDto> foundBookings = List.of(toDto(rejectedBooking));
        requestBooking.setState("REJECTED");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemUserUserIdAndStatusOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsNotValidReturnOneBooking() {
        requestBooking.setState("UNKNOWN");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        assertThrows(NotValidStateException.class, () -> bookingService.getBookingForOwner(requestBooking));
    }
}