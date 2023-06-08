package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Set;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(ItemRequest itemRequest, Long userId);

    ItemRequestDto updateItemRequest(ItemRequest itemRequest, Long userId, Long itemRequestId);

    Set<ItemRequestDto> getItemRequests();

    ItemRequestDto getItemRequestById(Long id);

    ItemRequestDto deleteItemRequest(ItemRequest itemRequest, Long userId);
}
