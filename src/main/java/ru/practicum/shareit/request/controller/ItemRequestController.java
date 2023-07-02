package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@Valid @RequestBody ItemRequestDto itemRequest,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.addItemRequest(itemRequest, userId);
    }

    @PatchMapping("/{id}")
    public ItemRequestDto updateItemRequest(@RequestBody ItemRequestDto itemRequest,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long id) {
        return itemRequestService.updateItemRequest(itemRequest, userId, id);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests() {
        return itemRequestService.getItemRequests();
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequest(@PathVariable Long id) {
        return itemRequestService.getItemRequestById(id);
    }

    @DeleteMapping("/id")
    public ItemRequestDto deleteItemRequest(@PathVariable("id") Long id,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.deleteItemRequest(id, userId);
    }
}
