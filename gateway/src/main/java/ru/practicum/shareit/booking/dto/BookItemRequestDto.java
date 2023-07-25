package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookItemRequestDto {
	@NotNull
	private long itemId;
	@FutureOrPresent
	@NotNull
	private LocalDateTime start;
	@Future
	@NotNull
	private LocalDateTime end;
}
