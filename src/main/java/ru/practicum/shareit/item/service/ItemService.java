package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Set;

public interface ItemService {

    ItemDto getItemById(Long id);

    Set<ItemDto> getItems(Long userId);

    ItemDto updateItem(Item item, Long userId, Long itemId);

    ItemDto addItem(Item item, Long userId);

    Set<ItemDto> searchItem(String text);

    ItemDto deleteItem(Item item, Long userId);
}
