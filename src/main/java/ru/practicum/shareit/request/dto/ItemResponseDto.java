package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ItemResponseDto {

    private Long id;
    private String name;
    private String description;
    private Long requestId;
    private Boolean available;
}