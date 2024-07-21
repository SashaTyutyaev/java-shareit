package ru.practicum.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Integer id;

    @NotNull
    @NotEmpty
    private String description;

    private Integer requestorId;

    private String created;
}
