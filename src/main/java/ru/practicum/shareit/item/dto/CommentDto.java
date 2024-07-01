package ru.practicum.shareit.item.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
public class CommentDto implements Serializable {

    private Integer id;

    private String authorName;

    private Integer itemId;

    @NotNull
    @NotEmpty
    private String text;

    private String created;
}
