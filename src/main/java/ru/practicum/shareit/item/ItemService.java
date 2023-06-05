package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long ownerId);

    ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId);

    ItemDtoWithBookings findItemById(long itemId, long userId);

    List<ItemDtoWithBookings> findAllByOwnerId(long ownerId);

    List<ItemDto> searchByText(String text);

    CommentDto addComment(long itemId, long userID, CommentDto commentDto);
}
