package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null);
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(
                0,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemDto.getRequest()
        );
    }

    public static ItemDtoWithBookings toItemDtoWithBookings(Item item) {
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings();
        itemDtoWithBookings.setId(item.getId());
        itemDtoWithBookings.setName(item.getName());
        itemDtoWithBookings.setDescription(item.getDescription());
        itemDtoWithBookings.setAvailable(item.getAvailable());

        return itemDtoWithBookings;
    }
}
