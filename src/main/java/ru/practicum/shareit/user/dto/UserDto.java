package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder
@Data
public class UserDto implements Serializable {
    private Integer id;
    @NotBlank
    @NotNull
    private String name;
    @Email
    @NotNull
    @NotBlank
    private String email;
}
