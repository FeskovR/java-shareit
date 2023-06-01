package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingShort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidationService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemValidationService itemValidationService;
    private final UserValidationService userValidationService;

    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        itemValidationService.validate(itemDto);
        userValidationService.checkUserIsExist(ownerId);
        Item item = ItemMapper.toItem(itemDto, userRepository.findById(ownerId).get());
        Item returnedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(returnedItem);
    }

    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        Optional<User> optionalUser = userRepository.findById(ownerId);
        if (optionalItem.isEmpty() || optionalUser.isEmpty()) {
            throw new NotFoundException("Item or User not found");
        } else {
            Item updatingItem = optionalItem.get();
            Item newItem = ItemMapper.toItem(itemDto, optionalUser.get());

            if (updatingItem.getOwner() != newItem.getOwner()) {
                throw new NotFoundException("User is not owner of this item");
            }

            if (newItem.getName() == null)
                newItem.setName(updatingItem.getName());
            if (newItem.getDescription() == null)
                newItem.setDescription(updatingItem.getDescription());
            if (newItem.getAvailable() == null)
                newItem.setAvailable(updatingItem.getAvailable());

            newItem.setId(updatingItem.getId());
            Item updatedItem = itemRepository.save(newItem);
            return ItemMapper.toItemDto(updatedItem);
        }
    }

    public ItemDtoWithBookings findItemById(long itemId, long userId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);

        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            ItemDtoWithBookings itemDtoWithBookings = setLastAndNextBookings(item, userId);
            return setCommentsToItemDto(itemDtoWithBookings);
        } else {
            throw new NotFoundException("Item not found");
        }
    }

    public List<ItemDtoWithBookings> findAllByOwnerId(long ownerId) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException("User not found");
        }

        List<Item> userItems = itemRepository.findAllByOwnerId(ownerId);
        List<ItemDtoWithBookings> userItemsDto = new ArrayList<>();

        for (Item userItem : userItems) {
            ItemDtoWithBookings itemDtoWithBookings = setLastAndNextBookings(userItem, ownerId);
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
        itemValidationService.checkItemIsExist(itemId);
        userValidationService.checkUserIsExist(userID);
        Item item = itemRepository.findById(itemId).get();
        User author = userRepository.findById(userID).get();
        itemValidationService.checkUserIsBookerOfItem(itemId, userID);
        itemValidationService.checkComment(commentDto);

        Comment comment = CommentMapper.toComment(commentDto, author, item, LocalDateTime.now());
        Comment returnedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(returnedComment);
    }

    private ItemDtoWithBookings setLastAndNextBookings(Item item, long userId) {
        ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(item);
        User owner = userRepository.findById(userId).get();
        if (item.getOwner().getId() != userId) {
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
}
