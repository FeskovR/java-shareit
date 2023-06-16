package ru.practicum.shareit.util;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.ItemRequestDto;

public class ItemRequestValidationService {
    public static void checkItemRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null ||
                itemRequestDto.getDescription().isBlank() ||
                itemRequestDto.getDescription().isEmpty()) {
            throw new ValidationException("Description cannot be empty");
        }
    }
}
