package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private User user2;
    private ItemDto itemDto;
    private Item item;
    private BookingDto bookingDto;
    private BookingDto bookingDto2;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, userRepository, itemRepository);
        user = User.builder()
                .id(1)
                .email("email@mail.ru")
                .name("name")
                .build();

        user2 = User.builder()
                .id(2)
                .email("email2@mail.ru")
                .name("name2")
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .name("name")
                .description("description")
                .requestId(1)
                .available(true)
                .build();

        item = ItemMapper.toItemFromDto(itemDto, user, null);

        bookingDto = BookingDto.builder()
                .id(1)
                .status(Status.WAITING)
                .bookerId(user.getId())
                .itemId(item.getId())
                .startDate(LocalDateTime.now().plusMinutes(30).toString())
                .endDate(LocalDateTime.now().plusHours(1).toString())
                .build();

        bookingDto2 = BookingDto.builder()
                .id(2)
                .status(Status.WAITING)
                .bookerId(user2.getId())
                .itemId(item.getId())
                .startDate(LocalDateTime.now().plusMinutes(30).toString())
                .endDate(LocalDateTime.now().plusHours(1).toString())
                .build();

        booking = BookingMapper.toBooking(bookingDto, item, user);
    }

    @Test
    void createBookingThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(bookingDto, 1));
    }

    @Test
    void createBookingThrowsEntityNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(bookingDto, 1));
    }

    @Test
    void createBookingThrowsEntityNotFoundExceptionWhenUserOwnerOfTheItem() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(bookingDto, 1));
    }

    @Test
    void createBookingSuccess() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        when(bookingRepository.save(any(Booking.class))).thenReturn(BookingMapper.toBooking(bookingDto, item, user2));

        BookingDto result = bookingService.createBooking(bookingDto, 2);

        assertNotNull(result);
        assertEquals(result.getId(), bookingDto.getId());
        assertEquals(result.getItemId(), bookingDto.getItemId());
        assertEquals(result.getBookerId(), bookingDto.getBookerId());
    }

    @Test
    void updateBookingThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.updateBooking(1, true, 1));
    }

    @Test
    void updateBookingThrowsEntityNotFoundExceptionWhenBookingNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.updateBooking(1, true, 1));
    }

    @Test
    void updateBookingsThrowsEntityNotFoundExceptionWhenUserIsNotOwnerOfTheItem() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> bookingService.updateBooking(1, true, 2));
    }

    @Test
    void updateBookingThrowsIncorrectParameterExceptionWhenStatusIsApproved() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        booking.setStatus(Status.APPROVED);

        assertThrows(IncorrectParameterException.class, () -> bookingService.updateBooking(1, true, 1));
    }

    @Test
    void updateBookingApprovedSuccess() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);

        booking.setStatus(Status.WAITING);
        assertEquals(booking.getStatus(), Status.WAITING);

        BookingDto result = bookingService.updateBooking(1, true, 1);

        assertNotNull(result);
        assertEquals(result.getId(), booking.getId());
        assertEquals(result.getItemId(), item.getId());
        assertEquals(result.getBookerId(), user.getId());
        assertEquals(result.getStatus(), Status.APPROVED);
    }

    @Test
    void updateBookingRejectedSuccess() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);

        booking.setStatus(Status.WAITING);
        assertEquals(booking.getStatus(), Status.WAITING);

        BookingDto result = bookingService.updateBooking(1, false, 1);

        assertNotNull(result);
        assertEquals(result.getId(), booking.getId());
        assertEquals(result.getItemId(), item.getId());
        assertEquals(result.getBookerId(), user.getId());
        assertEquals(result.getStatus(), Status.REJECTED);
    }

    @Test
    void getBookingByItemOwnerOrBookerThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingByItemOwnerOrBooker(1, 1));
    }

    @Test
    void getBookingByItemOwnerOrBookerThrowsEntityNotFoundExceptionBookingUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingByItemOwnerOrBooker(1, 1));
    }

    @Test
    void getBookingByItemOwnerOrBookerThrowsEntityNotFoundExceptionWhenUserIsNotOwnerOfTheItemOrNotOwnerOfTheBooking() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingByItemOwnerOrBooker(1, 2));
    }

    @Test
    void getBookingByItemOwnerOrBookerSuccess() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingByItemOwnerOrBooker(1, 1);

        assertNotNull(result);
        assertEquals(result.getId(), booking.getId());
        assertEquals(result.getItemId(), booking.getItem().getId());
        assertEquals(result.getBookerId(), booking.getBooker().getId());
    }

    @Test
    void getAllByBookerIdThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllByBookerId(1, "past", 1, 10));
    }

    @Test
    void getAllByBookerIdThrowsIncorrectParameterExceptionWhenPageableInvalid() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(IncorrectParameterException.class, () -> bookingService.getAllByBookerId(1, "past", -1, -1));
        assertThrows(IncorrectParameterException.class, () -> bookingService.getAllByBookerId(1, "past", 1, -1));
        assertThrows(IncorrectParameterException.class, () -> bookingService.getAllByBookerId(1, "past", -1, 5));
    }

    @Test
    void getAllByBookerIdThrowsIllegalArgumentExceptionWhenStateParameterInvalid() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> bookingService.getAllByBookerId(1, "papapa", 1, 10));
    }

    @Test
    void getAllByBookerIdSuccessWhenStateAll() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerId(anyInt(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByBookerId(1, "ALL", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerId(anyInt(), any(Pageable.class));
    }

    @Test
    void getAllByBookerIdSuccessWhenStateCurrent() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdCurrent(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByBookerId(1, "CURRENT", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdCurrent(anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerIdSuccessWhenStatePast() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndEndDateIsBefore(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByBookerId(1, "PAST", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdAndEndDateIsBefore(anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerIdSuccessWhenStateFuture() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndStartDateIsAfter(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByBookerId(1, "FUTURE", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdAndStartDateIsAfter(anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerIdSuccessWhenStatusWaiting() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndStatus(anyInt(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByBookerId(1, "WAITING", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdAndStatus(anyInt(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllByBookerIdSuccessWhenStatusRejected() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdAndStatus(anyInt(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByBookerId(1, "REJECTED", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdAndStatus(anyInt(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerIdThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllByOwnerId(1, "past", 1, 10));
    }

    @Test
    void getAllByOwnerIdThrowsIncorrectParameterExceptionWhenPageableInvalid() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(IncorrectParameterException.class, () -> bookingService.getAllByOwnerId(1, "past", -1, -1));
        assertThrows(IncorrectParameterException.class, () -> bookingService.getAllByOwnerId(1, "past", 1, -1));
        assertThrows(IncorrectParameterException.class, () -> bookingService.getAllByOwnerId(1, "past", -1, 5));
    }

    @Test
    void getAllByOwnerIdThrowsIllegalArgumentExceptionWhenStateParameterInvalid() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> bookingService.getAllByOwnerId(1, "papapa", 1, 10));
    }

    @Test
    void getAllByOwnerIdSuccessWhenStateAll() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItemOwner(anyInt(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerId(1, "ALL", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwner(anyInt(), any(Pageable.class));
    }

    @Test
    void getAllByOwnerIdSuccessWhenStateCurrent() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllCurrentBookingByOwnerId(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerId(1, "CURRENT", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllCurrentBookingByOwnerId(anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerIdSuccessWhenStatePast() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItemOwnerIdAndEndDateIsBefore(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerId(1, "PAST", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdAndEndDateIsBefore(anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerIdSuccessWhenStateFuture() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItemOwnerIdAndStartDateIsAfter(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerId(1, "FUTURE", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdAndStartDateIsAfter(anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerIdSuccessWhenStatusWaiting() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyInt(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerId(1, "WAITING", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdAndStatus(anyInt(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerIdSuccessWhenStatusRejected() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyInt(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerId(1, "REJECTED", 1, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdAndStatus(anyInt(), any(Status.class), any(Pageable.class));
    }
}