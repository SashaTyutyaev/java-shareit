package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static BookingDto toBookingDto(Booking booking) {

        String startDate = booking.getStartDate().format(DATE_TIME_FORMATTER);
        String endDate = booking.getEndDate().format(DATE_TIME_FORMATTER);

        return BookingDto.builder()
                .id(booking.getId())
                .bookerDto(UserMapper.toUserDto(booking.getBooker()))
                .itemDto(ItemMapper.toItemDto(booking.getItem()))
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .endDate(endDate)
                .startDate(startDate)
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {

        LocalDateTime startDate = LocalDateTime.parse(bookingDto.getStartDate(), DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(bookingDto.getEndDate(), DATE_TIME_FORMATTER);

        return Booking.builder()
                .id(bookingDto.getId())
                .startDate(startDate)
                .endDate(endDate)
                .item(item)
                .booker(booker)
                .build();
    }

    public static ShortBookingDto toShortBooking(Booking booking) {
        return ShortBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .endDate(booking.getEndDate())
                .startDate(booking.getStartDate())
                .build();
    }
}
