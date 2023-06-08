package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Booking {

    private Long id;
    private Long author;
    @NotNull
    private Long itemId;
    @NotNull
    @Future
    private LocalDateTime startBookingDate;
    @NotNull
    @Future
    private LocalDateTime endBookingDate;
    private Boolean confirmed;
}
