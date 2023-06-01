package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemValidationService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    public void validate(ItemDto itemDto) {
        if (itemDto.getName() == null ||
            itemDto.getName().isBlank() ||
            itemDto.getDescription() == null ||
            itemDto.getDescription().isBlank() ||
            itemDto.getAvailable() == null) {
            throw new ValidationException("Не хватает данных");
        }
    }

    public void checkItemIsExist(long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);

        if (item.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
    }

    public void checkUserIsBookerOfItem(long itemId, long userId) {
        User user = userService.findUserById(userId);
        Item item = itemRepository.findById(itemId).get();
        LocalDateTime now = LocalDateTime.now();

        long count = bookingRepository.countListOfBookersForItem(item, now, user);

        if (count <= 0)
            throw new ValidationException("This user cannot send comment to this item");
    }

    public void checkComment(CommentDto commentDto) {
        if (commentDto.getText().isBlank() || commentDto.getText().isEmpty()) {
            throw new ValidationException("Comment must contains text");
        }
    }
}
