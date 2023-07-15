package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemDtoMapper {

    public static ItemDto toDto(Item item) {
        Long itemId = item.getItemId();
        String name = item.getName();
        User owner = item.getUser();
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        Long requestId = item.getRequestId();

        return new ItemDto(itemId, name, UserDtoMapper.toDto(owner), description, available, requestId);
    }

    public static Item toItem(ItemDto item, User owner) {
        Long itemId = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        Long requestId = item.getRequestId();

        return new Item(itemId, name, owner, description, available, requestId);
    }

    public static Item toItem(ItemBookingDto itemDto, User user) {
        Long id = itemDto.getId();
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();
        Long requestId = itemDto.getRequestId();

        return new Item(id, name, user, description, available, requestId);
    }

    public static List<ItemDto> toDto(List<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            itemDtos.add(toDto(item));
        }

        return itemDtos;
    }

    public static ItemBookingDto fromItem(Item item) {
        Long itemId = item.getItemId();
        String name = item.getName();
        UserDto user = UserDtoMapper.toDto(item.getUser());
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        Long itemRequestId = item.getRequestId();

        return new ItemBookingDto(itemId, name, user, description, available, itemRequestId);
    }
}