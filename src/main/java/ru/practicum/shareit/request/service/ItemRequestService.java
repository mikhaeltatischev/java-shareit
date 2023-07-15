package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.GetItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(ItemRequestDto itemRequest, Long userId);

    List<ItemRequestDto> getItemRequestsForUser(Long userId);

    ItemRequestDto getItemRequestById(Long id, Long userId);

    ItemRequestDto deleteItemRequest(Long id, Long userId);

    List<ItemRequestDto> getItemRequests(GetItemRequest request);
}
