package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * TODO Sprint add-controllers.
 */
@Data
@SuperBuilder
@Jacksonized
public class ItemDto implements Serializable {
    private Integer id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private String description;

    private User owner;

    @NotNull
    private Boolean available;
}
