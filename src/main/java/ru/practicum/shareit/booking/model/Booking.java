package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Table(name = "bookings")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"bookingId"})
public class Booking implements Comparable<Booking> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column
    @NotNull
    @Future
    private LocalDateTime startTime;

    @Column
    @NotNull
    @Future
    private LocalDateTime endTime;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Override
    public int compareTo(Booking booking) {
        return booking.getEndTime().compareTo(this.endTime);
    }
}
