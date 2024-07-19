package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

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
    @NotEmpty
    private String description;

    private Integer ownerId;

    @NotNull
    private Boolean available;

    private Integer requestId;
}
