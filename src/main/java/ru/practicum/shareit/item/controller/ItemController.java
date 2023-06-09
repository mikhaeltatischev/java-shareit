package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemBookingDto getItemById(@PathVariable("itemId") Long itemId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemBookingDto> getItemsForUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsForUser(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Long itemId,
                              @RequestBody ItemDto item,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(item, userId, itemId);
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto item,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addItem(item, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto comment,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("itemId") Long itemId) {
        return itemService.addComment(comment, userId, itemId);
    }
}
