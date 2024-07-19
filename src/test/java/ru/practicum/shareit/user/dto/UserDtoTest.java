package ru.practicum.shareit.user.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> tester;

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1)
                .name("name")
                .email("name@mail.ru")
                .build();

        String json = "{\"id\":1,\"name\":\"name\",\"email\":\"name@mail.ru\"}";

        assertThat(tester.write(userDto)).isEqualToJson(json);
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"name\",\"email\":\"name@mail.ru\"}";

        UserDto userDto = UserDto.builder()
                .id(1)
                .name("name")
                .email("name@mail.ru")
                .build();

        assertThat(tester.parse(json)).usingRecursiveComparison().isEqualTo(userDto);
    }

}