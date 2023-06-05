package ru.practicum.shareit.util;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;

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
