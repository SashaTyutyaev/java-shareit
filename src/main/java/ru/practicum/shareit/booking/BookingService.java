package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Integer userId) {

        User user = getUserById(userId);
        Item item = getItemById(bookingDto.getItemId());
        bookingDto.setBookerId(userId);
        validateEndAndStartDate(bookingDto);
        validateItemAvailability(item);

        if (item.getOwner().getId().equals(userId)) {
            log.error("The owner of the item cannot create booking");
            throw new EntityNotFoundException("The owner of the item cannot create booking");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        booking.setStatus(Status.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto updateBooking(Integer bookingId, boolean approved, Integer userId) {
        getUserById(userId);
        Booking foundBooking = getBookingById(bookingId);
        if (!foundBooking.getItem().getOwner().getId().equals(userId)) {
            log.error("The user with id {} is not owner of the item with id {}", userId, foundBooking.getItem().getId());
            throw new EntityNotFoundException("The user with id " + userId + " is not owner of the item with id " + foundBooking.getItem().getId());
        }
        if (foundBooking.getStatus().equals(Status.APPROVED)) {
            log.error("The booking with id {} is already approved", bookingId);
            throw new IncorrectParameterException("The booking with id " + bookingId + "is already approved");
        }

        if (approved) {
            foundBooking.setStatus(Status.APPROVED);
        } else {
            foundBooking.setStatus(Status.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(foundBooking));
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingByItemOwnerOrBooker(Integer bookingId, Integer userId) {
        Booking foundBooking = getBookingById(bookingId);
        if (!foundBooking.getBooker().getId().equals(userId) &&
                !foundBooking.getItem().getOwner().getId().equals(userId)) {
            log.error("User with id {} is not owner of the booking with id {}", userId, bookingId);
            throw new EntityNotFoundException("User with id " + userId + " is not owner of the booking with id " + bookingId);
        }
        return BookingMapper.toBookingDto(foundBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getAllByBookerId(Integer userId, String state) {
        List<Booking> bookings;
        try {
            getUserById(userId);
            State bookingState = State.valueOf(state);
            Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
            bookings = getBookingsByBooker(bookingState, userId, sort);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }

        return bookings.stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getAllByOwnerId(Integer ownerId, String state) {
        List<Booking> bookings;
        try {
            getUserById(ownerId);
            State bookingState = State.valueOf(state);
            Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
            bookings = getBookingsByOwnerId(ownerId, bookingState, sort);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }

        return bookings.stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> getBookingsByBooker(State state, Integer userId, Sort sort) {
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, sort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndDateIsBefore(userId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartDateIsAfter(userId, LocalDateTime.now(), sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdCurrent(userId, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, sort);
        }
        return bookings;
    }

    private List<Booking> getBookingsByOwnerId(Integer userId, State state, Sort sort) {
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwner(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentBookingByOwnerId(userId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartDateIsAfter(userId, LocalDateTime.now(), sort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndDateIsBefore(userId, LocalDateTime.now(), sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING, sort);
                break;
        }
        return bookings;
    }

    private void validateEndAndStartDate(BookingDto bookingDto) {
        Booking booking = BookingMapper.toBooking(bookingDto, null, null);

        if (booking.getStartDate().isBefore(LocalDateTime.now())) {
            log.error("Start date must be in future");
            throw new IncorrectParameterException("Start date must be in future");
        }

        if (booking.getEndDate().isBefore(booking.getStartDate())) {
            log.error("End date must be after start date");
            throw new IncorrectParameterException("End date must be after start date");
        }

        if (booking.getEndDate().isEqual(booking.getStartDate())) {
            log.error("End date must not be equal to start date");
            throw new IncorrectParameterException("End date must not be equal to start date");
        }
    }

    private void validateItemAvailability(Item item) {
        if (!item.getAvailable()) {
            log.error("Item with id {} must be available", item.getId());
            throw new IncorrectParameterException("Item must be available");
        }
    }


    private Booking getBookingById(Integer id) {
        return bookingRepository.findById(id).orElseThrow(() -> {
            log.error("The booking with id {} not found", id);
            return new EntityNotFoundException("The booking with id " + id + " not found");
        });
    }


    private User getUserById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("The user with id {} is not found", userId);
            return new EntityNotFoundException("The user with id " + userId + " is not found");
        });
    }

    private Item getItemById(Integer itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("The item with id {} is not found", itemId);
            return new EntityNotFoundException("The item with id " + itemId + " is not found");
        });
    }

}