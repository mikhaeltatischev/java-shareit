package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto addItemRequest(ItemRequest itemRequest, Long userId) {
        return itemRequestRepository.addItemRequest(itemRequest, userId);
    }

    @Override
    public ItemRequestDto updateItemRequest(ItemRequest itemRequest, Long userId, Long itemRequestId) {
        return itemRequestRepository.updateItemRequest(itemRequest, userId, itemRequestId);
    }

    @Override
    public Set<ItemRequestDto> getItemRequests() {
        return itemRequestRepository.getItemRequests();
    }

    @Override
    public ItemRequestDto getItemRequestById(Long id) {
        return itemRequestRepository.getItemRequestById(id);
    }

    @Override
    public ItemRequestDto deleteItemRequest(ItemRequest itemRequest, Long userId) {
        return itemRequestRepository.deleteItemRequest(itemRequest, userId);
    }
}
