package ru.practicum.shareit.booking.repostitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking as b " +
            "join b.user as u " +
            "where u.userId = ?1 " +
            "order by b.startTime desc")
    List<Booking> findAllBookingByUserId(Long userId);

    List<Booking> findAllByUserUserIdOrderByEndTimeDesc(Long userId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.userId = ?1 " +
            "order by b.startTime desc")
    List<Booking> findAllBookingByOwnerId(Long ownerId);

    List<Booking> findAllByUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(Long userId, LocalDateTime one, LocalDateTime two);

    List<Booking> findAllByUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(Long userId, LocalDateTime time);

    List<Booking> findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(Long userId, LocalDateTime time, Status status);

    List<Booking> findAllByItemUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(Long userId, LocalDateTime time, Status status);

    List<Booking> findAllByUserUserIdAndStatusOrderByEndTimeDesc(Long userId, Status status);

    List<Booking> findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(Long userId, LocalDateTime time);

    List<Booking> findAllByItemUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(Long userId, LocalDateTime one, LocalDateTime two);

    List<Booking> findAllByItemUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(Long userId, LocalDateTime time);

    List<Booking> findAllByItemUserUserIdAndStatusOrderByEndTimeDesc(Long userId, Status status);

    List<Booking> findAllByItemUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(Long userId, LocalDateTime time);

    List<Booking> findAllByItemItemId(Long itemId);
}
