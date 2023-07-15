package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repostitory.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@DataJpaTest
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookingRepositoryIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    private long userId;
    private long ownerId;
    private PageRequest defaultPageRequest;
    private LocalDateTime now;

    @BeforeEach
    public void setUp() {
        ownerId = 1L;
        userId = 2L;
        defaultPageRequest = PageRequest.of(0, 10);
        now = LocalDateTime.now();
    }

    @Test
    public void findAllByUserUserIdOrderByEndTimeDescWhenFoundTwoBookingsReturnFourBookings() {
        List<Booking> bookings = bookingRepository.findAllByUserUserIdOrderByEndTimeDesc(userId, defaultPageRequest);

        assertEquals(4, bookings.size());
        assertEquals(1L, bookings.get(3).getBookingId());
        assertEquals(3L, bookings.get(2).getBookingId());
        assertEquals(2L, bookings.get(1).getBookingId());
        assertEquals(4L, bookings.get(0).getBookingId());
    }

    @Test
    public void findAllByUserUserIdOrderByEndTimeDescWhenBookingsNotFoundReturnEmptyList() {
        long userIdWithoutBookings = 1L;

        List<Booking> bookings = bookingRepository.findAllByUserUserIdOrderByEndTimeDesc(userIdWithoutBookings, defaultPageRequest);

        assertEquals(0, bookings.size());
    }

    @Test
    public void findAllBookingByOwnerIdWhenInvokedMethodReturnFourBookings() {
        List<Booking> bookings = bookingRepository.findAllBookingByOwnerId(ownerId, defaultPageRequest);

        assertEquals(4, bookings.size());
    }

    @Test
    public void findAllBookingByOwnerIdWhenOwnerDoesntHaveBookingsReturnEmptyList() {
        long ownerIdWithoutBookings = 2L;

        List<Booking> bookings = bookingRepository.findAllBookingByOwnerId(ownerIdWithoutBookings, defaultPageRequest);

        assertEquals(0, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDescWhenInvokedMethodReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(userId,
                        now, now, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndStartTimeIsAfterOrderByEndTimeDescWhenInvokedMethodReturnTwoBookings() {
        List<Booking> bookings = bookingRepository
                .findAllByUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(userId, now, defaultPageRequest);

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDescWhenStatusIsApprovedReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(userId, now, Status.APPROVED, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDescWhenStatusIsCanceledReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(userId, now, Status.CANCELED, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByItemUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDescWhenStatusIsApprovedReturnOneBookings() {
        List<Booking> bookings = bookingRepository
                .findAllByItemUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(ownerId, now, Status.APPROVED, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndStatusOrderByEndTimeDescWhenInvokedMethodReturnThreeBookings() {
        List<Booking> bookings = bookingRepository
                .findAllByUserUserIdAndStatusOrderByEndTimeDesc(userId, Status.APPROVED, defaultPageRequest);

        assertEquals(3, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDescWhenInvokedMethodReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(userId, now, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDescWhenInvokedMethodWithoutPageRequestReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(userId, now);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByItemUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc() {
        List<Booking> bookings = bookingRepository
                .findAllByItemUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(ownerId, now, now, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByItemUserUserIdAndStatusOrderByEndTimeDescWhenStatusIsApprovedReturnThreeBookings() {
        List<Booking> bookings = bookingRepository
                .findAllByItemUserUserIdAndStatusOrderByEndTimeDesc(ownerId, Status.APPROVED, defaultPageRequest);

        assertEquals(3, bookings.size());
    }

    @Test
    public void findAllByItemUserUserIdAndStatusOrderByEndTimeDescWhenStatusIsCanceledReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByItemUserUserIdAndStatusOrderByEndTimeDesc(ownerId, Status.CANCELED, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByItemUserUserIdAndEndTimeIsBeforeOrderByEndTimeDescWhenInvokedMethodReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByItemUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(ownerId, now, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByItemItemIdWhenMethodInvokedReturnFourBookings() {
        List<Booking> bookings = bookingRepository.findAllByItemItemId(1L);

        assertEquals(4, bookings.size());
    }

    @Test
    public void findAllByItemItemIdWhenItemDoesntHaveBookingsReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findAllByItemItemId(2L);

        assertEquals(0, bookings.size());
    }

    @Test
    public void findAllByItemsWhenInvokedMethodReturnFourBookings() {
        List<Item> items = itemRepository.findAll();

        List<Booking> bookings = bookingRepository.findAllByItems(items);

        assertEquals(4, bookings.size());
    }
}