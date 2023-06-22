package ru.practicum.shareit.request;

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
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    String headerTitle = "x-sharer-user-id";
    User requestor = new User(1L, "User1", "mail@mail.mail");
    ItemRequestDto itemRequestDto = new ItemRequestDto("Item desc");
    ItemRequest itemRequest = new ItemRequest(1L, itemRequestDto.getDescription(), requestor, LocalDateTime.now());
    ItemRequestDtoWithItems itemRequestDtoWithItems = new ItemRequestDtoWithItems(1L,
            itemRequest.getDescription(),
            itemRequest.getCreated(),
            new ArrayList<>());

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void addItemRequestTest() throws Exception {
        when(requestService.addItemRequest(any(), anyLong())).thenReturn(itemRequest);

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(headerTitle, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllUserRequestsTest() throws Exception {
        when(requestService.findAllUserRequests(anyLong())).thenReturn(List.of(itemRequestDtoWithItems));

        mvc.perform(get("/requests")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(headerTitle, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllRequestsTest() throws Exception {
        when(requestService.findAllRequests(anyLong(), anyLong(), anyLong())).thenReturn(List.of(itemRequestDtoWithItems));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(headerTitle, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void findRequestByIdTest() throws Exception {
        when(requestService.findItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestDtoWithItems);

        mvc.perform(get("/requests/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(headerTitle, "1"))
                .andExpect(status().isOk());
    }
}
