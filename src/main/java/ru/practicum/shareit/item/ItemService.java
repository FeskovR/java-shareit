package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private long id = 1;

    public Item addItem(ItemDto itemDto, long ownerId) {
        ItemValidationService.validate(itemDto, ownerId);

        Item item = ItemMapper.toItem(itemDto);
        item.setId(id++);
        item.setOwner(ownerId);

        return itemStorage.addItem(item);
    }

    public Item updateItem(ItemDto itemDto, long ownerId, long itemId) {
        Item item = itemStorage.findItemById(itemId);
        ItemValidationService.checkOwnerId(ownerId);

        if (item == null)
            throw new NotFoundException("Вещь для обновления не найдена");
        if (ownerId != item.getOwner())
            throw new NotFoundException("Эта вещь вам не принадлежит");

        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());

        return itemStorage.updateItem(item);
    }

    public Item findItemById(long itemId) {
        Item item = itemStorage.findItemById(itemId);

        if (item == null)
            throw new NotFoundException("Вещь не найдена");

        return item;
    }

    public List<Item> findAllByOwnerId(long ownerId) {
        ItemValidationService.checkOwnerId(ownerId);
        return itemStorage.findAllItemsByUserId(ownerId);
    }

    public List<Item> searchByText(String text) {
        List<Item> itemList = itemStorage.findAll();
        List<Item> resultList = new ArrayList<>();

        if (text == null || text.isBlank() || text.isEmpty())
            return resultList;

        for (Item item : itemList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                item.getAvailable()) {
                resultList.add(item);
            }
        }

        return resultList;
    }
}
