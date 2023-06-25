package ru.practicum.shareit.util;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.CommentDto;

public class ItemValidationService {

    public static void validate(ItemDto itemDto) {
        if (itemDto.getName() == null ||
            itemDto.getName().isBlank() ||
            itemDto.getDescription() == null ||
            itemDto.getDescription().isBlank() ||
            itemDto.getAvailable() == null) {
            throw new ValidationException("Не хватает данных");
        }
    }

    public static void checkComment(CommentDto commentDto) {
        if (commentDto.getText().isBlank() || commentDto.getText().isEmpty()) {
            throw new ValidationException("Comment must contains text");
        }
    }
}
