package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(author)
                .item(item)
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {

        String createdDate = DATE_TIME_FORMATTER.format(LocalDateTime.now());

        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItem().getId())
                .created(createdDate)
                .text(comment.getText())
                .build();
    }
}
