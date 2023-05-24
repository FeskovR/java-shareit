package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemValidationService {
    public void validate(ItemDto itemDto) {
        if (itemDto.getName() == null ||
            itemDto.getName().isBlank() ||
            itemDto.getDescription() == null ||
            itemDto.getDescription().isBlank() ||
            itemDto.getAvailable() == null) {
            throw new ValidationException("Не хватает данных");
        }
    }
//
//    public static void checkOwnerId(long ownerId) {
//        if (ownerId == 0) {
//            throw new ItemOwnerValidationException("Не указан владелец");
//        }
//    }
//
//    public static boolean isOwnerNotExist(long ownerId) {
//        User owner = userRepository.findUserById(ownerId);
//        return owner == null;
//    }
}
