package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable("itemId") Long itemId,
                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Set<Item> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@PathVariable("itemId") Long itemId,
                           @RequestBody Item item,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(item, userId, itemId);
    }

    @PostMapping
    public Item addItem(@Valid @RequestBody Item item,
                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addItem(item, userId);
    }

    @DeleteMapping
    public Item deleteItem(@RequestBody Item item,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.deleteItem(item, userId);
    }

    @GetMapping("/search")
    public Set<Item> search(@RequestParam("text") String text,
                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.searchItem(text);
    }
}
