package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ItemDto {

    private Long id;
    @NotBlank
    private String name;
    private UserDto user;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
}
