package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @MockBean
    ItemService itemService;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    String headerTitle = "x-sharer-user-id";
    User owner = new User(1L, "User1", "zz@zz.ru");
    User booker = new User(2L, "User2", "xx@xx.xx");
    Item item = new Item(1L, "Item1", "Desc1", true, owner, 0);
    BookingDto bookingDto = new BookingDto(1L,
            LocalDateTime.of(2099, 12, 12, 12, 12),
            LocalDateTime.of(2100, 12,12,12,12));
    Booking booking = new Booking(1L,
            bookingDto.getStart(),
            bookingDto.getEnd(),
            item,
            booker,
            Status.WAITING);

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void addBookingTest() throws Exception {
        when(bookingService.addBooking(any(), anyLong())).thenReturn(booking);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(booking))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(headerTitle, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void setOwnersDecisionTest() throws Exception {
        when(bookingService.setOwnersDecision(anyLong(), anyBoolean(), anyLong())).thenReturn(booking);

        mvc.perform(patch("/bookings/1?approved=true")
                .content(mapper.writeValueAsString(booking))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(headerTitle, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));
    }

    @Test
    void findBookingByIdTest() throws Exception {
        when(bookingService.findBookingById(anyLong(), anyLong())).thenReturn(booking);

        mvc.perform(get("/bookings/1")
                .content(mapper.writeValueAsString(booking))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(headerTitle, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));
    }

    @Test
    void findAllByBookerTest() throws Exception {
        when(bookingService.findAllByBooker(anyLong(), any(), anyLong(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings?state=ALL")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(headerTitle, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByOwnerTest() throws Exception {
        when(bookingService.findAllByOwner(anyLong(), any(), anyLong(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner?state=ALL")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(headerTitle, "1"))
                .andExpect(status().isOk());
    }
}
