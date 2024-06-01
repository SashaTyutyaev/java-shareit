package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean isRent;
    private User owner;
}
