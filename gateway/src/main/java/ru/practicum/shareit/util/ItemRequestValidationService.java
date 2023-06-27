package ru.practicum.shareit.util;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.RequestDto;

public class ItemRequestValidationService {
    public static void checkItemRequest(RequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null ||
                itemRequestDto.getDescription().isBlank() ||
                itemRequestDto.getDescription().isEmpty()) {
            throw new ValidationException("Description cannot be empty");
        }
    }
}
