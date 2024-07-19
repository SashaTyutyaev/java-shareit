package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForOwnerDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    private ItemRequestDto itemRequestDto;
    private ItemRequestForOwnerDto itemRequestForOwnerDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = generator.nextObject(ItemRequestDto.class);
        itemRequestForOwnerDto = ItemRequestForOwnerDto.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    @Test
    void postRequestSuccess() throws Exception {
        when(itemRequestService.addItemRequest(itemRequestDto, 1)).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()));
    }

    @Test
    void postRequestThrows400WhenDescriptionInvalid() throws Exception {
        itemRequestDto.setDescription("");
        when(itemRequestService.addItemRequest(itemRequestDto, 1)).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsByOwnerSuccess() throws Exception {
        when(itemRequestService.getAllItemRequestsByOwner(anyInt())).thenReturn(List.of(itemRequestForOwnerDto));

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(List.of(itemRequestForOwnerDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getRequestByIdSuccess() throws Exception {
        when(itemRequestService.getRequestById(anyInt(), anyInt())).thenReturn(itemRequestForOwnerDto);

        mvc.perform(get("/requests/{id}", itemRequestForOwnerDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestForOwnerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestForOwnerDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestForOwnerDto.getDescription()));
    }

    @Test
    void getPageableRequests() throws Exception {
        when(itemRequestService.getAllRequestsPageable(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemRequestForOwnerDto));

        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .content(objectMapper.writeValueAsString(List.of(itemRequestForOwnerDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}