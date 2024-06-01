package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private Integer id;
    @NotBlank
    private String name;
    @Email
    @NotNull
    private String email;
}
