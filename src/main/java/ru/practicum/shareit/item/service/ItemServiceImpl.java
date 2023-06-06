package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item getItemById(Long id) {
        return itemRepository.getItemById(id);
    }

    @Override
    public Set<Item> getItems(Long userId) {
        return itemRepository.getItems(userId);
    }

    @Override
    public Item updateItem(Item item, Long userId, Long itemId) {
        return itemRepository.updateItem(item, userId, itemId);
    }

    @Override
    public Item addItem(Item item, Long userId) {
        userRepository.getUserById(userId);
        return itemRepository.addItem(item, userId);
    }

    @Override
    public Set<Item> searchItem(String text) {
        return itemRepository.searchItem(text);
    }

    @Override
    public Item deleteItem(Item item, Long userId) {
        return itemRepository.deleteItem(item, userId);
    }
}
