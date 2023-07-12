package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.GetItem;

import java.util.List;

public interface ItemService {

    ItemBookingDto getItemById(Long id, Long userId);

    List<ItemBookingDto> getItemsForUser(GetItem item);

    ItemDto updateItem(ItemDto item, Long userId, Long itemId);

    ItemDto addItem(ItemDto item, Long userId);

    ItemDto deleteItem(Long itemId, Long userId);

    List<ItemDto> searchItem(GetItem item);

    CommentDto addComment(CommentDto comment, Long userId, Long itemId);
}
