package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toItemRequestDtoWithItems() {
        User user = new User(1L, "Name", "mail@mail.zz");
        ItemRequest itemRequest = new ItemRequest(1L, "Desc", user, LocalDateTime.now());

        ItemRequestDtoWithItems itemRequestDtoWithItems = ItemRequestMapper.toItemRequestDtoWithItems(itemRequest,
                new ArrayList<>());

        assertEquals(itemRequest.getDescription(), itemRequestDtoWithItems.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDtoWithItems.getCreated());
    }

    @Test
    void toItemRequest() {
        User user = new User(1L, "Name", "mail@mail.zz");
        ItemRequestDto itemRequestDto = new ItemRequestDto("Desc");

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user, LocalDateTime.now());

        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
    }
}