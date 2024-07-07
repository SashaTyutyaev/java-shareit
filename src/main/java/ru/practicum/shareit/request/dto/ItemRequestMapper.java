package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
public class ItemRequestMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(requestor)
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        String createdDate = DATE_TIME_FORMATTER.format(LocalDateTime.now());

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor() != null ? itemRequest.getRequestor().getId() : null)
                .created(createdDate)
                .build();
    }

    public static ItemRequestForOwnerDto toItemRequestForOwnerDto(ItemRequest itemRequest) {
        String createdDate = DATE_TIME_FORMATTER.format(itemRequest.getCreatedDate());

        return ItemRequestForOwnerDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .created(createdDate)
                .build();
    }


}
