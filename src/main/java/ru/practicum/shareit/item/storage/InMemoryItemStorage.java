package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryItemStorage implements ItemStorage{
    Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findItemById(long itemId) {
        return items.getOrDefault(itemId, null);
    }

    @Override
    public List<Item> findAllItemsByUserId(long userId) {
        List<Item> itemsList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == userId)
                itemsList.add(item);
        }
        return itemsList;
    }

    @Override
    public Item searchItem(String query) {
        return null;
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }
}
