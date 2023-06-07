package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemAlreadyExistsException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private static Long itemId = 1L;
    private Set<Item> items = new HashSet<>();

    @Override
    public ItemDto getItemById(Long id) {
        Item item = findItem(id);
        log.info("Found item: " + item);

        return createItemDto(item);
    }

    @Override
    public Set<ItemDto> getItems(Long userId) {
        Set<ItemDto> itemsDto = new HashSet<>();

        items.stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .forEach(item -> itemsDto.add(createItemDto(item)));

        return itemsDto;
    }

    @Override
    public ItemDto updateItem(Item item, Long userId, Long itemId) {
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
        log.info("Item with id: " + itemId + " updated");

        return createItemDto(currentItem);
    }

    @Override
    public ItemDto addItem(Item item, Long userId) {
        checkOnExist(item);
        item.setId(itemId++);
        item.setOwnerId(userId);
        items.add(item);

        return createItemDto(item);
    }

    @Override
    public Set<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new HashSet<>();
        }

        Set<ItemDto> itemsDto = new HashSet<>();

        items.stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
                        && item.getAvailable().equals(true))
                .forEach(item -> itemsDto.add(createItemDto(item)));

        return itemsDto;
    }

    @Override
    public ItemDto deleteItem(Item item, Long userId) {
        findItem(item.getId());
        checkItemOwner(item, userId);
        items.remove(item);
        log.info("Item with id: " + item.getId() + " removed");

        return createItemDto(item);
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

    private ItemDto createItemDto(Item item) {
        return new ItemDto(item.getId(), item.getOwnerId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    private void checkOnExist(Item item) {
        if (items.stream()
                .anyMatch(currentItem -> currentItem.getId().equals(item.getId()))) {
            throw new ItemAlreadyExistsException("Item with id: " + item.getId() + " already exists");
        }
    }
}
