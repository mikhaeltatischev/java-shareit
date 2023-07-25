package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.common.NotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.RequestItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemDtoMapper.*;

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
        List<Booking> bookings = bookingRepository.findAllByItemItemId(id);
        List<CommentDto> comments = CommentDtoMapper.toDto(commentRepository.findAllCommentByItemItemId(id));
        ItemBookingDto itemDto = fromItem(item);

        if (userId.equals(item.getUser().getUserId()) && !bookings.isEmpty()) {
            findLastAndNextBooking(itemDto, bookings);
        }
        itemDto.setComments(comments);

        log.info("Found item: " + item);

        return itemDto;
    }

    @Override
    public List<ItemBookingDto> getItemsForUser(RequestItem item) {
        Long userId = item.getUserId();
        PageRequest pageRequest = PageRequest.of(item.getFrom() / item.getSize(), item.getSize());

        List<Item> items = itemRepository.findAllByUserUserId(userId, pageRequest);
        List<Booking> bookings = bookingRepository.findAllByItems(items);
        List<CommentDto> comments = CommentDtoMapper.toDto(commentRepository.findAllByItems(items));
        List<ItemBookingDto> itemDtos = new ArrayList<>();

        for (Item currentItem : items) {
            Long itemId = currentItem.getItemId();
            ItemBookingDto itemDto = fromItem(currentItem);

            List<Booking> bookingsForItem = bookings.stream()
                    .filter(booking -> itemId.equals(booking.getItem().getItemId()))
                    .collect(Collectors.toList());

            List<CommentDto> commentsForItem = comments.stream()
                    .filter(commentDto -> itemId.equals(commentDto.getItemId()))
                    .collect(Collectors.toList());

            if (userId.equals(currentItem.getUser().getUserId()) && !bookings.isEmpty()) {
                findLastAndNextBooking(itemDto, bookingsForItem);
            }

            itemDto.setComments(commentsForItem);
            itemDtos.add(itemDto);
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
        update(item, itemDto);
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
    public ItemDto deleteItem(Long itemId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        checkOwner(user, item);

        itemRepository.delete(item);
        log.info("Item with id: " + itemId + " removed");

        return toDto(item);
    }

    @Override
    public List<ItemDto> searchItem(RequestItem item) {
        String text = item.getText();
        PageRequest pageRequest = PageRequest.of(item.getFrom() / item.getSize(), item.getSize());

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> items = itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                        text, text, pageRequest);

        log.info("Found items: " + items);

        return toDto(items);
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (userId.equals(item.getUser().getUserId())) {
            throw new CommentCreateException("The Owner of the item can not add comment");
        }

        List<Booking> bookings = bookingRepository.findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(userId, LocalDateTime.now());

        bookings.stream()
                .filter((booking) -> userId.equals(booking.getUser().getUserId()))
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

    private void findLastAndNextBooking(ItemBookingDto item, List<Booking> bookings) {
        Booking lastBooking = bookings.stream()
                .filter(booking -> booking.getStartTime().isBefore(LocalDateTime.now()) && !Status.REJECTED.equals(booking.getStatus()))
                .min(Booking::compareTo)
                .orElse(null);

        Booking nextBooking = bookings.stream()
                .filter((booking) -> booking.getStartTime().isAfter(LocalDateTime.now()) && !Status.REJECTED.equals(booking.getStatus()))
                .max(Booking::compareTo)
                .orElse(null);

        item.setLastBooking(lastBooking == null ? null : BookingDtoMapper.toDto(lastBooking));
        item.setNextBooking(nextBooking == null ? null : BookingDtoMapper.toDto(nextBooking));
    }
}