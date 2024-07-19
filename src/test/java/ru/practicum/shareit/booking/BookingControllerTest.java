package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final EasyRandom generator = new EasyRandom();

    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = generator.nextObject(BookingDto.class);
    }

    @Test
    void createBookingSuccess() throws Exception {
        when(bookingService.createBooking(any(BookingDto.class), anyInt())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    void createBookingThrows400WhenItemIdInvalid() throws Exception {
        bookingDto.setItemId(null);
        when(bookingService.createBooking(any(BookingDto.class), anyInt())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookingSuccess() throws Exception {
        when(bookingService.updateBooking(anyInt(), anyBoolean(), anyInt())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{id}", bookingDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    void getBookingByIdSuccess() throws Exception {
        when(bookingService.getBookingByItemOwnerOrBooker(anyInt(), anyInt())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{id}", bookingDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    void getBookingsByBookerIdSuccess() throws Exception {
        when(bookingService.getAllByBookerId(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(List.of(bookingDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }

    @Test
    void getBookingsByItemOwner() throws Exception {
        when(bookingService.getAllByOwnerId(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(List.of(bookingDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
    }
}