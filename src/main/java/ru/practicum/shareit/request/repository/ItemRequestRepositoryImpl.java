package ru.practicum.shareit.request.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestAlreadyExistsException;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class ItemRequestRepositoryImpl implements ItemRequestRepository {

    private static Long itemRequestId = 1L;
    private final Set<ItemRequest> itemRequests = new HashSet<>();

    @Override
    public ItemRequestDto addItemRequest(ItemRequest itemRequest, Long userId) {
        checkOnExist(itemRequest);
        itemRequest.setId(itemRequestId++);
        itemRequest.setAuthorId(userId);
        itemRequest.setTimeOfCreation(LocalDateTime.now());
        itemRequests.add(itemRequest);
        log.info("ItemRequest: " + itemRequest + " saved");

        return createItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto updateItemRequest(ItemRequest itemRequest, Long userId, Long itemRequestId) {
        ItemRequest currentItemRequest = findItemRequest(itemRequestId);
        checkOwnerId(currentItemRequest, userId);

        if (itemRequest.getId() != null) {
            currentItemRequest.setId(itemRequest.getId());
        }

        if (itemRequest.getItemName() != null) {
            currentItemRequest.setItemName(itemRequest.getItemName());
        }

        if (itemRequest.getAuthorId() != null) {
            currentItemRequest.setAuthorId(itemRequest.getAuthorId());
        }
        log.info("Item request with id: " + itemRequestId + " updated");

        return createItemRequestDto(currentItemRequest);
    }

    @Override
    public Set<ItemRequestDto> getItemRequests() {
        Set<ItemRequestDto> itemRequestsDto = new HashSet<>();

        itemRequests.stream()
                .forEach(itemRequest -> itemRequestsDto.add(createItemRequestDto(itemRequest)));

        return itemRequestsDto;
    }

    @Override
    public ItemRequestDto getItemRequestById(Long id) {
        ItemRequest itemRequest = findItemRequest(id);
        log.info("Item request with id: " + id + ": " + itemRequest);

        return createItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto deleteItemRequest(ItemRequest itemRequest, Long userId) {
        findItemRequest(itemRequest.getId());
        checkOwnerId(itemRequest, userId);
        itemRequests.remove(itemRequest);
        log.info("Item request with id: " + itemRequest.getId() + " deleted");

        return createItemRequestDto(itemRequest);
    }

    private ItemRequest findItemRequest(Long id) {
        return itemRequests.stream()
                .filter(itemRequest -> itemRequest.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ItemRequestNotFoundException("ItemRequest with id: " + id
                        + " not found"));
    }

    private ItemRequestDto createItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getItemName(), itemRequest.getAuthorId(),
                itemRequest.getTimeOfCreation());
    }

    private void checkOwnerId(ItemRequest itemRequest, Long userId) {
        if (!itemRequest.getAuthorId().equals(userId)) {
            throw new NotOwnerException("User with id: " + userId + " is not the owner this item request");
        }
    }

    private void checkOnExist(ItemRequest request) {
         if (itemRequests.stream()
                 .anyMatch(itemRequest -> itemRequest.getId().equals(request.getId()))) {
             throw new ItemRequestAlreadyExistsException("Item request with id: " + request.getId() + " already exists");
         }
    }
}
