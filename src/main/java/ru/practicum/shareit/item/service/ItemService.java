package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Set;

public interface ItemService {

    Item getItemById(Long id);

    Set<Item> getItems();

    Item updateItem(Item item, Long userId);

    Item addItem(Item item, Long userId);

    Set<Item> searchItem(String text);

    Item deleteItem(Item item, Long userId);
}
