package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Set;

public interface ItemRepository {

    Item getItemById(Long id);

    Set<Item> getItems(Long userId);

    Item updateItem(Item item, Long userId, Long itemId);

    Item addItem(Item item, Long userId);

    Set<Item> searchItem(String text);

    Item deleteItem(Item item, Long userId);
}
