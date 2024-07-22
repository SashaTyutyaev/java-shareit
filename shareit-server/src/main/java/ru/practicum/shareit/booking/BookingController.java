package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @PathVariable Integer bookingId,
                                    @RequestParam boolean approved) {
        return bookingService.updateBooking(bookingId, approved, userId);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBookingById(@PathVariable Integer bookingId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.getBookingByItemOwnerOrBooker(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByItemOwner(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                   @RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllByOwnerId(userId, state, from, size);

    }

}
