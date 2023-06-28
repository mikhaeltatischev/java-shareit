package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestDtoMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        Long id = itemRequest.getItemRequestId();
        String name = itemRequest.getItemName();
        Long authorId = itemRequest.getAuthor().getUserId();
        LocalDateTime timeOfCreation = itemRequest.getTimeOfCreation();

        return new ItemRequestDto(id, name, authorId, timeOfCreation);
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User author) {
        Long id = itemRequestDto.getId();
        String name = itemRequestDto.getItemName();
        LocalDateTime timeOfCreation = itemRequestDto.getTimeOfCreation();

        return new ItemRequest(id, name, author, timeOfCreation);
    }

    public static List<ItemRequestDto> toDto(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(toDto(itemRequest));
        }

        return itemRequestDtos;
    }
}
