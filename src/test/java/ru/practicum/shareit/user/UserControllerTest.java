package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = generator.nextObject(UserDto.class);
    }

    @Test
    void addUserSuccess() throws Exception {
        userDto.setEmail("user@mail.ru");
        when(userService.addUser(userDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));
    }

    @Test
    void addUserThrows400WhenEmailInvalid() throws Exception {
        when(userService.addUser(userDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserThrows400WhenNameInvalid() throws Exception {
        userDto.setEmail("user@mail.ru");
        userDto.setName("");
        when(userService.addUser(userDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserSuccess() throws Exception {
        when(userService.updateUser(any(User.class), anyInt())).thenReturn(userDto);

        mvc.perform(patch("/users/{id}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));
    }

    @Test
    void getAllUsersSuccess() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(userDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getUserByIdSuccess() throws Exception {
        when(userService.getUserDtoById(anyInt())).thenReturn(userDto);

        mvc.perform(get("/users/{id}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));
    }

    @Test
    void deleteUserById() throws Exception {
        mvc.perform(delete("/users/{id}", userDto.getId()))
                .andExpect(status().isOk());
    }
}