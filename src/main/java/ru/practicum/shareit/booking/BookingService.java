package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Booking createBooking(@Valid Booking booking, Integer userId) {
        if (booking.getEndDate().isBefore(LocalDate.now())) {
            log.error("End date must be in future");
            throw new IncorrectParameterException("End date must be in future");
        }

        if (booking.getStartDate().isBefore(LocalDate.now())) {
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

        User user = getUserById(userId);
        Booking savedBooking = bookingRepository.save(booking);
        savedBooking.setBooker(user);
        savedBooking.setStatus(Status.WAITING);
        Item item = getItemById(savedBooking.getItem().getId());
        return savedBooking;
    }

    public Booking updateBooking(Integer bookingId, Boolean isApproved) {
        Booking foundBooking = getBookingById(bookingId);
        if (!foundBooking.getBooker().getId().equals(bookingId)) {
            log.error("The booker with id {} is not owner of the booking with id {}", foundBooking.getBooker().getId(), bookingId);
            throw new IncorrectParameterException("The booker with id " + foundBooking.getBooker().getId() + " is not owner of the booking with id " + bookingId);
        }

        if (isApproved == null) {
            log.error("The booking with id {} is not approved or rejected", bookingId);
            throw new IncorrectParameterException("The booking with id " + bookingId + " is not approved or rejected");
        }

        if (isApproved) {
            foundBooking.setStatus(Status.APPROVED);
        }

        if (!isApproved) {
            foundBooking.setStatus(Status.REJECTED);
        }

        return foundBooking;
    }

    public Booking getBookingByItemOwnerOrBooker(Integer bookingId, Integer userId) {
        Booking foundBooking = getBookingById(bookingId);
        if (!foundBooking.getBooker().getId().equals(userId) ||
                !foundBooking.getItem().getOwner().getId().equals(userId)) {
            log.error("User with id {} is not owner of the booking with id {}", userId, bookingId);
            throw new EntityNotFoundException("User with id " + userId + " is not owner of the booking with id " + bookingId);
        }
        return foundBooking;
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
