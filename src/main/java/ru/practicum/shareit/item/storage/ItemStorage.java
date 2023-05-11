package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item findItemById(long itemId);

    List<Item> findAllItemsByUserId(long userId);

    Item searchItem(String query);

    List<Item> findAll();
}
