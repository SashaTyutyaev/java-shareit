package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;

    private static final Sort SORT = Sort.by(Sort.Direction.ASC, "id");


    @Override
    public ItemDto addItem(ItemDto item, Integer userId) {
        User user = getUserById(userId);
        Item itemToSave = ItemMapper.toItemFromDto(item, user);
        itemToSave.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(itemToSave));
    }

    @Override
    public ItemDto updateItem(ItemDto item, Integer itemId, Integer userId) {
        User user = getUserById(userId);
        Item optItem = getItemById(itemId);
        if (!optItem.getOwner().equals(user)) {
            log.error("The item with id {} is not owned by user {}", itemId, userId);
            throw new EntityNotFoundException("The item with id " + itemId + " is not owned by user " + userId);
        }

        if (item.getAvailable() != null) {
            optItem.setAvailable(item.getAvailable());
        }

        if (item.getName() != null) {
            optItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            optItem.setDescription(item.getDescription());
        }
        return ItemMapper.toItemDto(itemRepository.saveAndFlush(optItem));
    }

    @Override
    public List<ItemForOwnerDto> getAllItemsOfUser(Integer userId) {
        getUserById(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId, SORT);

        List<ItemForOwnerDto> itemForOwnerDtoList = new ArrayList<>();

        for (Item item : items) {
            ItemForOwnerDto itemForOwnerDto = ItemMapper.toItemForOwnerDto(item);
            setBookingsToItem(userId, item, itemForOwnerDto);
            setCommentsToItem(item, itemForOwnerDto);
            itemForOwnerDtoList.add(itemForOwnerDto);
        }

        return itemForOwnerDtoList;
    }

    @Override
    public ItemForOwnerDto getItemDtoById(Integer itemId, Integer userId) {
        getUserById(userId);
        Item item = getItemById(itemId);

        ItemForOwnerDto itemForOwnerDto = ItemMapper.toItemForOwnerDto(item);

        setBookingsToItem(userId, item, itemForOwnerDto);
        setCommentsToItem(item, itemForOwnerDto);
        return itemForOwnerDto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.findByText(text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Integer userId, Integer itemId) {
        User author = getUserById(userId);
        Item item = getItemById(itemId);

        List<BookingDto> bookingDtos = bookingService.getAllByBookerId(userId, State.PAST.name());

        boolean isOwnerOfTheItemBookings = bookingDtos.stream()
                .anyMatch(bookingDto -> bookingDto.getItemId().equals(itemId)
                        && bookingDto.getStatus().equals(Status.APPROVED));

        if (!isOwnerOfTheItemBookings) {
            log.error("The item with id {} has not bookings by user {}", itemId, userId);
            throw new IncorrectParameterException("The item with id " + itemId + " has not bookings by user " + userId);
        }

        Comment comment = CommentMapper.toComment(commentDto, author, item);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Item getItemById(Integer itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("The item with id {} is not found", itemId);
            return new EntityNotFoundException("The item with id " + itemId + " is not found");
        });
    }

    private User getUserById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("The user with id {} is not found", userId);
            return new EntityNotFoundException("The user with id " + userId + " is not found");
        });
    }

    private Booking getLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStartDate().isBefore(LocalDateTime.now()))
                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                .max(Comparator.comparing(Booking::getStartDate))
                .orElse(null);
    }

    private Booking getNextBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStartDate().isAfter(LocalDateTime.now()))
                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                .min(Comparator.comparing(Booking::getStartDate))
                .orElse(null);
    }

    private void setBookingsToItem(Integer userId, Item item, ItemForOwnerDto itemForOwnerDto) {
        if (item.getOwner().getId().equals(userId)) {

            final Sort itemSort = Sort.by(Sort.Direction.ASC, "id");

            List<Booking> bookings = bookingRepository.findAllByItemId(item.getId(), itemSort);

            if (!bookings.isEmpty()) {
                Booking lastBooking = getLastBooking(bookings);
                Booking nextBooking = getNextBooking(bookings);

                if (nextBooking != null) {
                    itemForOwnerDto.setNextBooking(BookingMapper.toShortBooking(nextBooking));
                }
                if (lastBooking != null) {
                    itemForOwnerDto.setLastBooking(BookingMapper.toShortBooking(lastBooking));
                }
            } else {
                log.info("There is no bookings for item with id {}", item.getId());
            }
        } else {
            log.info("User with id {} is not owner of the item with id {} " +
                    "so the list will be displayed without bookings", userId, item.getId());
        }
    }

    private void setCommentsToItem(Item item, ItemForOwnerDto itemForOwnerDto) {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        itemForOwnerDto.setComments(commentDtos);
    }
}
