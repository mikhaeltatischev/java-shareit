package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Set;

public interface ItemRepository {

    Item getItemById(Long id);

    Set<Item> getItems();

    Item updateItem(Item item, Long userId);

    Item addItem(Item item, Long userId);

    Set<Item> searchItem(String text);

    Item deleteItem(Item item, Long userId);
}
