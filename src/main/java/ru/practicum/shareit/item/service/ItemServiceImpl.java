package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repostitory.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CommentCreateException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemDtoMapper.toDto;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.toItem;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemBookingDto getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        ItemBookingDto itemDto;
        List<Booking> bookings = bookingRepository.findAllByItemItemId(id);
        List<CommentDto> comments = CommentDtoMapper.toDto(commentRepository.findAllCommentByItemItemId(id));


        if (item.getUser().getUserId().equals(userId) && !bookings.isEmpty()) {
            Booking lastBooking = bookings.stream()
                    .filter(booking -> booking.getStartTime().isBefore(LocalDateTime.now()) && !booking.getStatus().equals(Status.REJECTED))
                    .min(Booking::compareTo)
                    .orElse(null);

            Booking nextBooking = bookings.stream()
                    .filter((booking) -> booking.getStartTime().isAfter(LocalDateTime.now()) && !booking.getStatus().equals(Status.REJECTED))
                    .max(Booking::compareTo)
                    .orElse(null);

            itemDto = toDto(item, lastBooking == null ? null : BookingDtoMapper.toDto(lastBooking),
                                  nextBooking == null ? null : BookingDtoMapper.toDto(nextBooking), comments);
        } else {
            itemDto = toDto(item, null, null, comments);
        }

        log.info("Found item: " + item);

        return itemDto;
    }

    @Override
    public List<ItemBookingDto> getItemsForUser(Long userId) {
        List<Item> items = itemRepository.findAllByUserUserId(userId);
        List<ItemBookingDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            itemDtos.add(getItemById(item.getItemId(), userId));
        }

        log.info("Found items: " + items);

        return itemDtos;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        checkOwner(user, item);
        item = update(item, itemDto);
        itemDto.setId(itemId);
        itemRepository.save(item);
        log.info("Item with id: " + itemId + " updated");

        return toDto(item);
    }

    @Override
    public ItemDto addItem(ItemDto item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Item savedItem = itemRepository.save(toItem(item, user));
        log.info("Item with id: " + savedItem.getItemId() + " saved");

        return toDto(savedItem);
    }

    @Override
    public void deleteItem(Long itemId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (!item.getUser().getUserId().equals(user.getUserId())) {
            throw new NotOwnerException("User with id: " + userId + " is not the owner Item with id: " + itemId);
        }

        itemRepository.delete(item);
        log.info("Item with id: " + itemId + " removed");
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> items = itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true, text, text);

        log.info("Found items: " + items);

        return toDto(items);
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (item.getUser().getUserId().equals(userId)) {
            throw new CommentCreateException("The Owner of the item can not add comment");
        }

        List<Booking> bookings = bookingRepository.findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(userId, LocalDateTime.now());

        bookings.stream()
                .filter((booking) -> booking.getUser().getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CommentCreateException("User with id: " + userId + " did not rent the item with id: " + itemId));

        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(CommentDtoMapper.toComment(commentDto, author, item));

        log.info("Comment with id: " + comment.getCommentId() + " saved");

        return CommentDtoMapper.toDto(comment);
    }

    private void checkOwner(User user, Item item) {
        if (!item.getUser().getUserId().equals(user.getUserId())) {
            throw new NotOwnerException("User with id: " + user.getUserId() + " is not the owner Item with id: " + item.getItemId());
        }
    }

    private Item update(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return item;
    }
}
