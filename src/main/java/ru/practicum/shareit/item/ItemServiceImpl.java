package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingShort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.ItemValidationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        ItemValidationService.validate(itemDto);
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        Item item = ItemMapper.toItem(itemDto, owner);
        Item returnedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(returnedItem);
    }

    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item not found")
        );
        User user = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        Item newItem = ItemMapper.toItem(itemDto, user);

        if (item.getOwner() != newItem.getOwner()) {
            throw new NotFoundException("User is not owner of this item");
        }

        if (newItem.getName() == null)
            newItem.setName(item.getName());
        if (newItem.getDescription() == null)
            newItem.setDescription(item.getDescription());
        if (newItem.getAvailable() == null)
            newItem.setAvailable(item.getAvailable());

        newItem.setId(item.getId());
        Item updatedItem = itemRepository.save(newItem);
        return ItemMapper.toItemDto(updatedItem);

    }

    public ItemDtoWithBookings findItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item not found")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        ItemDtoWithBookings itemDtoWithBookings = setLastAndNextBookings(item, user);
        return setCommentsToItemDto(itemDtoWithBookings);
    }

    public List<ItemDtoWithBookings> findAllByOwnerId(long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        List<Item> userItems = itemRepository.findAllByOwnerId(ownerId);
        List<ItemDtoWithBookings> userItemsDto = new ArrayList<>();

        for (Item userItem : userItems) {
            ItemDtoWithBookings itemDtoWithBookings = setLastAndNextBookings(userItem, owner);
            userItemsDto.add(setCommentsToItemDto(itemDtoWithBookings));
        }

        return userItemsDto;
    }

    public List<ItemDto> searchByText(String text) {
        List<Item> itemList = itemRepository.findAll();
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

    public CommentDto addComment(long itemId, long userID, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item not found")
        );
        User author = userRepository.findById(userID).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        checkUserIsBookerOfItem(item, author);
        ItemValidationService.checkComment(commentDto);

        Comment comment = CommentMapper.toComment(commentDto, author, item, LocalDateTime.now());
        Comment returnedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(returnedComment);
    }

    private ItemDtoWithBookings setLastAndNextBookings(Item item, User owner) {
        ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(item);
        if (item.getOwner().getId() != owner.getId()) {
            return itemDtoWithBookings;
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> futureBookingsByOwner = bookingRepository.getAllBookingsForNext(item, now, owner);
        List<Booking> pastBookingsByOwner = bookingRepository.getAllBookingsForLast(item, now, owner);
        if (pastBookingsByOwner.size() > 0) {
            BookingShort lastBooking = BookingMapper.toBookingShort(pastBookingsByOwner.get(0));
            itemDtoWithBookings.setLastBooking(lastBooking);
        }
        if (futureBookingsByOwner.size() > 0) {
            BookingShort nextBooking = BookingMapper.toBookingShort(futureBookingsByOwner.get(0));
            itemDtoWithBookings.setNextBooking(nextBooking);
        }

        return itemDtoWithBookings;
    }

    private ItemDtoWithBookings setCommentsToItemDto(ItemDtoWithBookings itemDtoWithBookings) {
        List<Comment> comments = commentRepository.findByItemId(itemDtoWithBookings.getId());
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(CommentMapper.toCommentDto(comment));
        }
        itemDtoWithBookings.setComments(commentDtos);

        return itemDtoWithBookings;
    }

    private void checkUserIsBookerOfItem(Item item, User user) {
        LocalDateTime now = LocalDateTime.now();

        long count = bookingRepository.countListOfBookersForItem(item, now, user);

        if (count <= 0)
            throw new ValidationException("This user cannot send comment to this item");
    }
}
