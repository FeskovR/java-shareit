package ru.practicum.shareit.request;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDtoWithItems toItemRequestDtoWithItems(ItemRequest itemRequest,
                                                                    List<Item> items) {
        ItemRequestDtoWithItems itemRequestDtoWithItems = new ItemRequestDtoWithItems();
        itemRequestDtoWithItems.setId(itemRequest.getId());
        itemRequestDtoWithItems.setDescription(itemRequest.getDescription());
        itemRequestDtoWithItems.setCreated(itemRequest.getCreated());
        itemRequestDtoWithItems.setItems(items);
        return itemRequestDtoWithItems;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto,
                                            User requestor,
                                            LocalDateTime now) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(now);
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }
}
