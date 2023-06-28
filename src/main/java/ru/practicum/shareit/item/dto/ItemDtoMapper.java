package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
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

        return new ItemDto(itemId, name, UserDtoMapper.toDto(owner), description, available);
    }

    public static Item toItem(ItemDto item, User owner) {
        Long itemId = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();

        return new Item(itemId, name, owner, description, available);
    }

    public static ItemBookingDto toDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
        Long itemId = item.getItemId();
        String name = item.getName();
        User owner = item.getUser();
        String description = item.getDescription();
        Boolean available = item.getAvailable();

        return new ItemBookingDto(itemId, name, UserDtoMapper.toDto(owner), description, available, lastBooking, nextBooking, comments);
    }

    public static List<ItemDto> toDto(List<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            itemDtos.add(toDto(item));
        }

        return itemDtos;
    }
}
