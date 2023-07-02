package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;


import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class CommentDto {

    private Long id;
    private UserDto author;
    private String authorName;
    private Long authorId;
    private int rating;
    @NotBlank
    private String text;
    private LocalDateTime created;
    private ItemDto item;
    private Long itemId;
}
