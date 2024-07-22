package ru.practicum.shareit.item.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ItemForOwnerDto extends ItemDto {

    private ShortBookingDto lastBooking;

    private ShortBookingDto nextBooking;

    private List<CommentDto> comments;

}
