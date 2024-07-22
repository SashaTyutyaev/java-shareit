package ru.practicum.shareit.item.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> tester;

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .ownerId(1)
                .requestId(1)
                .build();

        String json = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"ownerId\":1,\"requestId\":1}";

        assertThat(tester.write(itemDto)).isEqualToJson(json);
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"ownerId\":1,\"requestId\":1}";

        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .ownerId(1)
                .requestId(1)
                .build();

        assertThat(tester.parse(json)).usingRecursiveComparison().isEqualTo(itemDto);
    }
}