package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Builder
@Data
public class UserDto {
    private Integer id;
    @NotBlank
    @NotNull
    private String name;
    @Email
    @NotNull
    @NotBlank
    private String email;
    private List<ItemDto> userItems;
}
