package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@Builder
public class ItemDto {

    private Long id;
    private String name;
    private UserDto user;
    private String description;
    private Boolean available;
    private Long requestId;
}
