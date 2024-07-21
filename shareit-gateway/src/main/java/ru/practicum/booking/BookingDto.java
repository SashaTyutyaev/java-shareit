package ru.practicum.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.item.ItemDto;
import ru.practicum.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Builder
@Jacksonized
public class BookingDto implements Serializable {
    private Integer id;

    @NotNull
    private Integer itemId;

    @JsonProperty("item")
    private ItemDto itemDto;

    private Integer bookerId;

    @JsonProperty("booker")
    private User bookerDto;

    @JsonProperty("start")
    @NotNull
    @NotEmpty
    private String startDate;

    @JsonProperty("end")
    @NotNull
    @NotEmpty
    private String endDate;

    @Builder.Default
    private Status status = Status.WAITING;
}
