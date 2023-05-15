package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemOwnerNotExistException;
import ru.practicum.shareit.exceptions.ItemOwnerValidationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private long id = 1;

    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        validate(itemDto, ownerId);

        Item item = ItemMapper.toItem(itemDto);
        item.setId(id++);
        item.setOwner(ownerId);

        return ItemMapper.toItemDto(itemStorage.addItem(item));
    }

    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        Item item = itemStorage.findItemById(itemId);
        checkOwnerId(ownerId);

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

        return ItemMapper.toItemDto(itemStorage.updateItem(item));
    }

    public ItemDto findItemById(long itemId) {
        Item item = itemStorage.findItemById(itemId);

        if (item == null)
            throw new NotFoundException("Вещь не найдена");

        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> findAllByOwnerId(long ownerId) {
        checkOwnerId(ownerId);

        List<ItemDto> userItems = new ArrayList<>();
        for (Item item : itemStorage.findAllItemsByUserId(ownerId)) {
            userItems.add(ItemMapper.toItemDto(item));
        }
        return userItems;
    }

    public List<ItemDto> searchByText(String text) {
        List<Item> itemList = itemStorage.findAll();
        List<ItemDto> resultList = new ArrayList<>();

        if (text == null || text.isBlank() || text.isEmpty())
            return resultList;

        for (Item item : itemList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                item.getAvailable()) {
                resultList.add(ItemMapper.toItemDto(item));
            }
        }

        return resultList;
    }

    // методы для валидации
    public void validate(ItemDto itemDto, long ownerId) {
        checkOwnerId(ownerId);

        if (isOwnerNotExist(ownerId)) {
            throw new ItemOwnerNotExistException("Указанный владелец не зарегестрирован");
        }

        if (
                itemDto.getName() == null ||
                        itemDto.getName().isBlank() ||
                        itemDto.getDescription() == null ||
                        itemDto.getDescription().isBlank() ||
                        itemDto.getAvailable() == null
        ) {
            throw new ValidationException("Не хватает данных");
        }
    }

    public void checkOwnerId(long ownerId) {
        if (ownerId == 0) {
            throw new ItemOwnerValidationException("Не указан владелец");
        }
    }

    private boolean isOwnerNotExist(long ownerId) {
        User owner = userStorage.findUserById(ownerId);
        return owner == null;
    }
}
