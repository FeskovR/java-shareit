package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto() {
        User user = new User(1L, "User", "mail@zz.zz");
        Item item = new Item(1L, "Item", "Desc", true, user, 0);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
    }

    @Test
    void toItem() {
        User user = new User(1L, "User", "mail@zz.zz");
        ItemDto itemDto = new ItemDto(1L, "ItemDto", "Desc", true, 0);

        Item item = ItemMapper.toItem(itemDto, user);

        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(user.getId(), item.getOwner().getId());
    }

    @Test
    void toItemDtoWithBookings() {
        User user = new User(1L, "User", "mail@zz.zz");
        Item item = new Item(1L, "Item", "Desc", true, user, 0);

        ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(item);

        assertEquals(item.getDescription(), itemDtoWithBookings.getDescription());
        assertEquals(item.getAvailable(), itemDtoWithBookings.getAvailable());
    }
}