package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Builder
public class CommentDto {

    private Long id;
    private UserDto author;
    private String authorName;
    private Long authorId;
    private int rating;
    private String text;
    private LocalDateTime created;
    private ItemDto item;
    private Long itemId;
}
