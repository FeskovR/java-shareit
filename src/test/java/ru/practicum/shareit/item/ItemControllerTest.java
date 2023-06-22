package ru.practicum.shareit.item;

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
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    ItemDto itemDto1 = new ItemDto(0, "Item1", "Desc1", true, 0);
    ItemDto itemDto2 = new ItemDto(0, "Item2", "Desc2", true, 0);
    ItemDtoWithBookings itemDtoWithBookings1 = new ItemDtoWithBookings(1, "Item1", "Desc1", true, null, null, new ArrayList<>());
    CommentDto commentDto = new CommentDto(1, "Comment text", "", LocalDateTime.now());

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void addItemTest() throws Exception {
        when(itemService.addItem(any(), anyLong())).thenReturn(itemDto1);

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(itemDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-sharer-user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto1.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto1.getRequestId()));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(itemDto2);

        mvc.perform(patch("/items/1")
                .content(mapper.writeValueAsString(itemDto2))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-sharer-user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto2.getId()))
                .andExpect(jsonPath("$.name").value(itemDto2.getName()))
                .andExpect(jsonPath("$.description").value(itemDto2.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto2.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto2.getRequestId()));
    }

    @Test
    void findItemByIdTest() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong())).thenReturn(itemDtoWithBookings1);

        mvc.perform(get("/items/1")
                .content(mapper.writeValueAsString(itemDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-sharer-user-id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByOwnerIdTest() throws Exception {
        when(itemService.findAllByOwnerId(anyLong(), anyLong(), anyInt())).thenReturn(List.of(itemDtoWithBookings1));

        mvc.perform(get("/items")
                .content(mapper.writeValueAsString(itemDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-sharer-user-id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void searchByTextTest() throws Exception {
        when(itemService.searchByText(any(), anyLong(), anyInt())).thenReturn(List.of(itemDto1));

        mvc.perform(get("/items/search/?text=qqq")
                .content(mapper.writeValueAsString(itemDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-sharer-user-id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("x-sharer-user-id", "1"))
                .andExpect(status().isOk());
    }
}
