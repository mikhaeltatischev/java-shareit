package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.NotOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.GetItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.ItemRequestDtoMapper.toDto;
import static ru.practicum.shareit.request.dto.ItemRequestDtoMapper.toItemRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = checkExistsUser(userId);

        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestRepository.save(toItemRequest(itemRequestDto, user));
        log.info("Item request with id: " + itemRequest.getItemRequestId() + " saved");

        return toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequestsForUser(Long userId) {
        checkExistsUser(userId);
        List<ItemRequest> requests = itemRequestRepository.getItemRequestsByUserUserId(userId);
        List<ItemRequestDto> requestDtos = toDto(requests);

        List<Item> items = itemRepository.findAllByRequests(requests.stream()
                .map(ItemRequest::getItemRequestId)
                .collect(Collectors.toList()));

        addResponsesToRequests(requestDtos, items);

        return requestDtos.stream()
                .sorted(ItemRequestDto::compareTo)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long id, Long userId) {
        checkExistsUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new ItemRequestNotFoundException(id));
        List<Item> items = itemRepository.findAllByRequestId(id);

        ItemRequestDto itemRequestDto = toDto(itemRequest);

        List<ItemResponseDto> response = items.stream()
                .map(item -> new ItemResponseDto(item.getItemId(), item.getName(), item.getDescription(),
                        item.getRequestId(), item.getAvailable()))
                .collect(Collectors.toList());
        itemRequestDto.setItems(response);

        log.info("Item request with id: " + id + ": " + itemRequest);

        return itemRequestDto;
    }

    @Override
    public ItemRequestDto deleteItemRequest(Long id, Long userId) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new ItemRequestNotFoundException(id));
        User user = checkExistsUser(userId);
        checkAccess(itemRequest, user);

        log.info("Item request with id: " + id + " deleted");

        return toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequests(GetItemRequest request) {
        User user = checkExistsUser(request.getUserId());
        PageRequest pageable = PageRequest.of(request.getFrom(), request.getSize());

        List<ItemRequest> requests = itemRequestRepository.findAllByUserUserIdNotLike(user.getUserId(), pageable);
        List<ItemRequestDto> itemRequestDtos = toDto(requests);

        List<Item> items = itemRepository.findAllByRequests(requests.stream()
                .map(ItemRequest::getItemRequestId)
                .collect(Collectors.toList()));

        addResponsesToRequests(itemRequestDtos, items);

        log.info("Found item requests: " + requests);

        return itemRequestDtos;
    }

    private User checkExistsUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void checkAccess(ItemRequest itemRequest, User user) {
        if (!itemRequest.getUser().getUserId().equals(user.getUserId())) {
            throw new NotOwnerException("User with id + " + user.getUserId() + " not owner request with id: " + itemRequest.getItemRequestId());
        }
    }

    private void addResponsesToRequests(List<ItemRequestDto> itemRequestDtos, List<Item> items) {
        for (ItemRequestDto currentRequest : itemRequestDtos) {
            List<ItemResponseDto> response = items.stream()
                    .filter(item -> item.getRequestId().equals(currentRequest.getId()))
                    .map(item -> new ItemResponseDto(item.getItemId(), item.getName(), item.getDescription(),
                            item.getRequestId(), item.getAvailable()))
                    .collect(Collectors.toList());

            currentRequest.setItems(response);
        }
    }
}
