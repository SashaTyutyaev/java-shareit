package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    private ItemDto itemDto;
    private ItemForOwnerDto itemForOwnerDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = generator.nextObject(ItemDto.class);
        itemForOwnerDto = ItemForOwnerDto.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
        commentDto = generator.nextObject(CommentDto.class);
    }

    @Test
    void addItemSuccess() throws Exception {
        when(itemService.addItem(itemDto, 1)).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void addItemThrows400WhenNameInvalid() throws Exception {
        itemDto.setName("");
        when(itemService.addItem(itemDto, 1)).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemThrows400WhenDescriptionInvalid() throws Exception {
        itemDto.setDescription("");
        when(itemService.addItem(itemDto, 1)).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItemThrows400WhenAvailableInvalid() throws Exception {
        itemDto.setAvailable(null);
        when(itemService.addItem(itemDto, 1)).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateItemSuccess() throws Exception {
        when(itemService.updateItem(any(ItemDto.class), anyInt(), anyInt())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void getAllItemsSuccess() throws Exception {
        when(itemService.getAllItemsOfUser(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemForOwnerDto));

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(List.of(itemForOwnerDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(itemForOwnerDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemForOwnerDto.getName()));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemDtoById(anyInt(), any())).thenReturn(itemForOwnerDto);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemForOwnerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemForOwnerDto.getId()))
                .andExpect(jsonPath("$.name").value(itemForOwnerDto.getName()));
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "text")
                        .content(objectMapper.writeValueAsString(List.of(itemDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(itemForOwnerDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemForOwnerDto.getName()));
    }

    @Test
    void addCommentSuccess() throws Exception {
        when(itemService.addComment(any(CommentDto.class), anyInt(), anyInt())).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", commentDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }

    @Test
    void addCommentThrows400WhenTextInvalid() throws Exception {
        commentDto.setText("");
        when(itemService.addComment(any(CommentDto.class), anyInt(), anyInt())).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", commentDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }
}