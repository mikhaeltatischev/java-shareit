package ru.practicum.shareit.booking.repostitory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByUserUserIdOrderByEndTimeDesc(Long userId, PageRequest pageRequest);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.userId = ?1 " +
            "order by b.startTime desc")
    List<Booking> findAllBookingByOwnerId(Long ownerId, PageRequest pageRequest);

    List<Booking> findAllByUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(Long userId, LocalDateTime one, LocalDateTime two, PageRequest pageRequest);

    List<Booking> findAllByUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(Long userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(Long userId, LocalDateTime time, Status status, PageRequest pageRequest);

    List<Booking> findAllByItemUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(Long userId, LocalDateTime time, Status status, PageRequest pageRequest);

    List<Booking> findAllByUserUserIdAndStatusOrderByEndTimeDesc(Long userId, Status status, PageRequest pageRequest);

    List<Booking> findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(Long userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(Long userId, LocalDateTime time);

    List<Booking> findAllByItemUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(Long userId, LocalDateTime one, LocalDateTime two, PageRequest pageRequest);

    List<Booking> findAllByItemUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(Long userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByItemUserUserIdAndStatusOrderByEndTimeDesc(Long userId, Status status, PageRequest pageRequest);

    List<Booking> findAllByItemUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(Long userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByItemItemId(Long itemId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i in ?1")
    List<Booking> findAllByItems(List<Item> items);
}
