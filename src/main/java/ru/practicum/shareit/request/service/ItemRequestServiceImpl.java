package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.request.dto.ItemRequestDtoMapper.toDto;
import static ru.practicum.shareit.request.dto.ItemRequestDtoMapper.toItemRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        ItemRequest itemRequest = itemRequestRepository.save(toItemRequest(itemRequestDto, user));
        log.info("Item request with id: " + itemRequest.getItemRequestId() + " saved");

        return toDto(itemRequest);
    }

    @Override
    public ItemRequestDto updateItemRequest(ItemRequestDto itemRequestDto, Long userId, Long itemRequestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(itemRequestId));

        if (!itemRequest.getAuthor().getUserId().equals(user.getUserId())) {
            throw new NotOwnerException("User with id: " + user.getUserId() + " is not the owner Item request with id: " + itemRequestId);
        }

        itemRequestDto.setId(itemRequestId);
        ItemRequest updatedItemRequest = itemRequestRepository.save(toItemRequest(itemRequestDto, user));
        log.info("Item request with id: " + itemRequestId + " updated");

        return toDto(updatedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequests() {
        return toDto(itemRequestRepository.findAll());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new ItemRequestNotFoundException(id));

        log.info("Item request with id: " + id + ": " + itemRequest);

        return toDto(itemRequest);
    }

    @Override
    public ItemRequestDto deleteItemRequest(Long id, Long userId) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new ItemRequestNotFoundException(id));

        log.info("Item request with id: " + id + " deleted");

        return toDto(itemRequest);
    }
}
