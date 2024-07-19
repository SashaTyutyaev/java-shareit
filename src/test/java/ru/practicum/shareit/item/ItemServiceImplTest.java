package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private BookingService bookingService;

    private ItemDto itemDto;
    private Item item;
    private ItemRequest request;
    private User user;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, bookingService, itemRequestRepository);

        user = User.builder()
                .id(1)
                .email("email@mail.ru")
                .name("name")
                .build();

        request = ItemRequest.builder()
                .id(1)
                .requestor(user)
                .createdDate(LocalDateTime.now())
                .description("description")
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .name("name")
                .description("description")
                .requestId(1)
                .available(true)
                .ownerId(1)
                .build();

        item = ItemMapper.toItemFromDto(itemDto, user, request);

        booking = Booking.builder()
                .id(1)
                .status(Status.APPROVED)
                .booker(user)
                .item(item)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(1))
                .build();

        comment = Comment.builder()
                .id(1)
                .text("text")
                .author(user)
                .item(item)
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Test
    void addItemThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.addItem(itemDto, anyInt()));
    }

    @Test
    void addItemSuccess() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(request));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto result = itemService.addItem(itemDto, anyInt());

        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        assertEquals(result.getOwnerId(), item.getOwner().getId());
    }

    @Test
    void updateItemThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDto, 1, 1));
    }

    @Test
    void updateItemThrowsEntityNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDto, 1, 1));
    }

    @Test
    void updateItemThrowsEntityNotFoundExceptionWhenUserIsNotOwner() {
        item.setId(1);
        User userNotOwner = User.builder()
                .id(2)
                .email("email@mail.ru")
                .name("name")
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userNotOwner));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDto, 1, 2));
    }

    @Test
    void updateItemSuccessAllFields() {
        ItemDto updatedItem = ItemDto.builder()
                .available(false)
                .description("descriptionUpdate")
                .name("nameUpdate")
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(item)).thenReturn(item);

        ItemDto result = itemService.updateItem(updatedItem, 1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), updatedItem.getName());
        assertEquals(result.getDescription(), updatedItem.getDescription());
        assertEquals(result.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void updateItemSuccessOnlyName() {
        ItemDto updatedItem = ItemDto.builder()
                .name("nameUpdate")
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(item)).thenReturn(item);

        ItemDto result = itemService.updateItem(updatedItem, 1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), updatedItem.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
    }

    @Test
    void updateItemSuccessOnlyDescription() {
        ItemDto updatedItem = ItemDto.builder()
                .description("descriptionUpdate")
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(item)).thenReturn(item);

        ItemDto result = itemService.updateItem(updatedItem, 1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), updatedItem.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
    }

    @Test
    void updateItemSuccessOnlyAvailable() {
        ItemDto updatedItem = ItemDto.builder()
                .available(false)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(item)).thenReturn(item);

        ItemDto result = itemService.updateItem(updatedItem, 1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void getAllItemsOfUserThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getAllItemsOfUser(1, 1, 1));
    }

    @Test
    void getAllItemsOfUserThrowsIncorrectParameterExceptionWhenInvalidPageableParams() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(IncorrectParameterException.class, () -> itemService.getAllItemsOfUser(2, -1, 1));
        assertThrows(IncorrectParameterException.class, () -> itemService.getAllItemsOfUser(2, 0, -1));
        assertThrows(IncorrectParameterException.class, () -> itemService.getAllItemsOfUser(2, -1, 5));
        assertThrows(IncorrectParameterException.class, () -> itemService.getAllItemsOfUser(2, null, 5));
        assertThrows(IncorrectParameterException.class, () -> itemService.getAllItemsOfUser(2, -1, null));
    }

    @Test
    void getAllItemsOfUserSuccessWithoutBookingsAndComments() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemForOwnerDto> items = itemService.getAllItemsOfUser(1, 0, 10);
        assertNotNull(items);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), item.getId());
        assertEquals(items.get(0).getName(), item.getName());
        assertEquals(items.get(0).getDescription(), item.getDescription());
        assertTrue(items.get(0).getComments().isEmpty());
        assertNull(items.get(0).getNextBooking());
        assertNull(items.get(0).getLastBooking());
    }

    @Test
    void getAllItemsOfUserSuccessWithBookingsAndWithoutComments() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemId(anyInt(), any(Sort.class))).thenReturn(List.of(booking));

        List<ItemForOwnerDto> items = itemService.getAllItemsOfUser(1, 0, 10);

        assertNotNull(items);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), item.getId());
        assertEquals(items.get(0).getName(), item.getName());
        assertEquals(items.get(0).getDescription(), item.getDescription());
        assertEquals(items.get(0).getLastBooking(), BookingMapper.toShortBooking(booking));
        assertTrue(items.get(0).getComments().isEmpty());
        assertNull(items.get(0).getNextBooking());
    }

    @Test
    void getAllItemsOfUserSuccessWithBookingsAndComments() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemId(anyInt(), any(Sort.class))).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(List.of(comment));

        List<ItemForOwnerDto> items = itemService.getAllItemsOfUser(1, 0, 10);

        assertNotNull(items);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), item.getId());
        assertEquals(items.get(0).getName(), item.getName());
        assertEquals(items.get(0).getDescription(), item.getDescription());
        assertEquals(items.get(0).getLastBooking(), BookingMapper.toShortBooking(booking));
        assertEquals(items.get(0).getComments().get(0).getId(), comment.getId());
        assertEquals(items.get(0).getComments().get(0).getText(), comment.getText());
        assertEquals(items.get(0).getComments().get(0).getAuthorName(), comment.getAuthor().getName());
    }

    @Test
    void getAllItemsOfUserSuccessWithoutBookingsAndWithComments() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt(), any(Pageable.class))).thenReturn(List.of(item));
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(List.of(comment));

        List<ItemForOwnerDto> items = itemService.getAllItemsOfUser(1, 0, 10);

        assertNotNull(items);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), item.getId());
        assertEquals(items.get(0).getName(), item.getName());
        assertEquals(items.get(0).getDescription(), item.getDescription());
        assertEquals(items.get(0).getComments().get(0).getId(), comment.getId());
        assertEquals(items.get(0).getComments().get(0).getText(), comment.getText());
        assertEquals(items.get(0).getComments().get(0).getAuthorName(), comment.getAuthor().getName());
        assertNull(items.get(0).getLastBooking());
        assertNull(items.get(0).getNextBooking());

    }

    @Test
    void getItemDtoByIdThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemDtoById(1, 1));
    }

    @Test
    void getItemDtoByIdThrowsEntityNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemDtoById(1, 1));
    }

    @Test
    void getItemDtoByIdSuccessWithoutBookingsAndComments() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        ItemForOwnerDto result = itemService.getItemDtoById(1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        assertTrue(result.getComments().isEmpty());
        assertNull(result.getNextBooking());
        assertNull(result.getLastBooking());
    }

    @Test
    void getItemDtoByIdSuccessWithoutBookingsAndWithComments() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(List.of(comment));

        ItemForOwnerDto result = itemService.getItemDtoById(1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        assertEquals(result.getComments().get(0).getId(), comment.getId());
        assertEquals(result.getComments().get(0).getText(), comment.getText());
        assertEquals(result.getComments().get(0).getAuthorName(), comment.getAuthor().getName());
        assertNull(result.getNextBooking());
        assertNull(result.getLastBooking());
    }

    @Test
    void getItemDtoByIdSuccessWithBookingsAndWithoutComments() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemId(anyInt(), any(Sort.class))).thenReturn(List.of(booking));

        ItemForOwnerDto result = itemService.getItemDtoById(1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        assertEquals(result.getLastBooking(), BookingMapper.toShortBooking(booking));
        assertNull(result.getNextBooking());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void getItemDtoByIdSuccessWithBookingsComments() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemId(anyInt(), any(Sort.class))).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(List.of(comment));

        ItemForOwnerDto result = itemService.getItemDtoById(1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        assertEquals(result.getLastBooking(), BookingMapper.toShortBooking(booking));
        assertEquals(result.getComments().get(0).getId(), comment.getId());
        assertEquals(result.getComments().get(0).getText(), comment.getText());
        assertEquals(result.getComments().get(0).getAuthorName(), comment.getAuthor().getName());
        assertNull(result.getNextBooking());
    }

    @Test
    void searchItemsEmptyListWhenAllTextNullOrEmptyOrBlank() {
        List<ItemDto> itemsNullText = itemService.searchItems(null, 1, 10);

        assertNotNull(itemsNullText);
        assertTrue(itemsNullText.isEmpty());

        List<ItemDto> itemsEmptyText = itemService.searchItems("", 1, 10);

        assertNotNull(itemsEmptyText);
        assertTrue(itemsEmptyText.isEmpty());

        List<ItemDto> itemsBlankText = itemService.searchItems(" ", 1, 10);

        assertNotNull(itemsBlankText);
        assertTrue(itemsBlankText.isEmpty());
    }

    @Test
    void searchItemsThrowsIncorrectParameterExceptionWhenInvalidPageableParams() {
        assertThrows(IncorrectParameterException.class, () -> itemService.searchItems("text", -1, 5));
        assertThrows(IncorrectParameterException.class, () -> itemService.searchItems("text", -1, -1));
        assertThrows(IncorrectParameterException.class, () -> itemService.searchItems("text", 0, -1));
    }

    @Test
    void searchItemsSuccess() {
        when(itemRepository.findByText(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> items = itemService.searchItems("dEsC", 0, 10);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(items.get(0).getId(), item.getId());
        assertEquals(items.get(0).getDescription(), item.getDescription());
        assertEquals(items.get(0).getAvailable(), item.getAvailable());
    }

    @Test
    void addCommentThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(CommentMapper.toCommentDto(comment), 1, 1));
    }

    @Test
    void addCommentThrowsEntityNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(CommentMapper.toCommentDto(comment), 1, 1));
    }

    @Test
    void addCommentThrowsIncorrectParameterExceptionWhenUserHasNotBookings() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingService.getAllByBookerId(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        assertThrows(IncorrectParameterException.class, () -> itemService.addComment(CommentMapper.toCommentDto(comment), 1, 1));
    }

    @Test
    void addCommentSuccess() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingService.getAllByBookerId(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(Stream.of(booking).map(BookingMapper::toBookingDto).collect(Collectors.toList()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(CommentMapper.toCommentDto(comment), 1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), comment.getId());
        assertEquals(result.getAuthorName(), comment.getAuthor().getName());
        assertEquals(result.getItemId(), comment.getItem().getId());
        assertEquals(result.getText(), comment.getText());
    }

}