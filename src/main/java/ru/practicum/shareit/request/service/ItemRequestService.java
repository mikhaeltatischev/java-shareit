package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(ItemRequestDto itemRequest, Long userId);

    ItemRequestDto updateItemRequest(ItemRequestDto itemRequest, Long userId, Long itemRequestId);

    List<ItemRequestDto> getItemRequests();

    ItemRequestDto getItemRequestById(Long id);

    ItemRequestDto deleteItemRequest(Long id, Long userId);
}
