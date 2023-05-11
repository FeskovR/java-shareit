package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null);
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                0,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                0,
                itemDto.getRequest()
        );
    }
}