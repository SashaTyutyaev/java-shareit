package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class ShortBookingDto implements Serializable {

    private Integer id;

    private Integer bookerId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
