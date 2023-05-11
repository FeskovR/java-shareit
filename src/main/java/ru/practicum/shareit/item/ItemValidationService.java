package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemOwnerNotExistException;
import ru.practicum.shareit.exceptions.ItemOwnerValidationException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
public class ItemValidationService {
    private static ItemStorage itemStorage;
    private static UserStorage userStorage;

    ItemValidationService(ItemStorage itemStorage, UserStorage userStorage) {
        ItemValidationService.itemStorage = itemStorage;
        ItemValidationService.userStorage = userStorage;
    }

    public static void validate(ItemDto itemDto, long ownerId) {
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

    public static void checkOwnerId(long ownerId) {
        if (ownerId == 0) {
            throw new ItemOwnerValidationException("Не указан владелец");
        }
    }

    private static boolean isOwnerNotExist(long ownerId) {
        User owner = userStorage.findUserById(ownerId);
        return owner == null;
    }
}
