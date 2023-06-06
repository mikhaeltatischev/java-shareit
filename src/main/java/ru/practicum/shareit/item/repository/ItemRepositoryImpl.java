package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.ItemAlreadyExistsException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private static Long itemId = 1L;
    private Set<Item> items = new HashSet<>();

    @Override
    public Item getItemById(Long id) {
        Item item = findItem(id);
        log.info("Found item: " + item);

        return item;
    }

    @Override
    public Set<Item> getItems(Long userId) {
        return items.stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toSet());
    }

    @Override
    public Item updateItem(Item item, Long userId, Long itemId) {
        try {
            Item currentItem = findItem(itemId);
            checkItemOwner(currentItem, userId);

            if (item.getId() != null) {
                currentItem.setId(item.getId());
            }

            if (item.getOwnerId() != null) {
                currentItem.setOwnerId(item.getOwnerId());
            }

            if (item.getName() != null) {
                currentItem.setName(item.getName());
            }

            if (item.getDescription() != null) {
                currentItem.setDescription(item.getDescription());
            }

            if (item.getAvailable() != null) {
                currentItem.setAvailable(item.getAvailable());
            }

            return currentItem;
        } catch (ItemNotFoundException e) {
            log.info(e.getMessage());
            throw new ItemNotFoundException("Item with id: " + item.getId() + " not found");
        }
    }

    @Override
    public Item addItem(Item item, Long userId) {
        try {
            findItem(item.getId());
            log.info("Item with id: " + item.getId() + " already exists");
            throw new ItemAlreadyExistsException("Item with id: " + item.getId() + " already exists");
        } catch (ItemNotFoundException e) {
            item.setId(itemId++);
            item.setOwnerId(userId);
            items.add(item);
            return item;
        }
    }

    @Override
    public Set<Item> searchItem(String text) {
        if (text.isBlank()) {
            return new HashSet<>();
        }

        return items.stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
                        && item.getAvailable().equals(true))
                .collect(Collectors.toSet());
    }

    @Override
    public Item deleteItem(Item item, Long userId) {
        try {
            findItem(item.getId());
            checkItemOwner(item, userId);
            items.remove(item);
            log.info("Item with id: " + item.getId() + " removed");

            return item;
        } catch (ItemNotFoundException e) {
            log.info(e.getMessage());
            throw new ItemNotFoundException("Item with id: " + item.getId() + " not found");
        }
    }

    private Item findItem(Long id) {
        return items.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Item with id: " + id + " not found"));
    }

    private void checkItemOwner(Item item, Long userId) {
        if (!item.getOwnerId().equals(userId)) {
            throw new NotOwnerException("User with id: " + userId + " is not the owner of the item with id: " + item.getId());
        }
    }
}
