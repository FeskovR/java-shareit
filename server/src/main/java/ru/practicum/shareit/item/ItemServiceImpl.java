package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingShort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElse(null);
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
        ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElse(null);
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
        if (itemRequest != null)
            newItem.setRequestId(itemRequest.getId());

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

    public List<ItemDtoWithBookings> findAllByOwnerId(long ownerId, long from, int size) {
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        if (from < 0 || size < 1) {
            throw new ValidationException("Pageable validation error");
        }

        int page = (int) (from / size);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);

        List<Item> userItems = itemRepository.findAllByOwnerId(ownerId, pageable);
        List<ItemDtoWithBookings> userItemsDto = new ArrayList<>();

        Map<Long, Booking> pastBookings = new HashMap<>();
        List<Booking> pastBookingsList = bookingRepository.findPastBookingsByOwner(owner, LocalDateTime.now(), pageable).toList();

        for (Booking booking : pastBookingsList) {
            long itemId = booking.getItem().getId();
            if (pastBookings.get(itemId) == null) {
                pastBookings.put(itemId, booking);
            } else {
                Booking prevBooking = pastBookings.get(itemId);
                if (booking.getStart().isAfter(prevBooking.getStart())) {
                    pastBookings.put(itemId, booking);
                }
            }
        }

        Map<Long, Booking> nextBookings = new HashMap<>();
        List<Booking> nextBookingsList = bookingRepository.findFutureBookingsByOwner(owner, LocalDateTime.now(), pageable).toList();

        for (Booking booking : nextBookingsList) {
            long itemId = booking.getItem().getId();
            if (nextBookings.get(itemId) == null) {
                nextBookings.put(itemId, booking);
            } else {
                Booking prevBooking = nextBookings.get(itemId);
                if (booking.getStart().isBefore(prevBooking.getStart())) {
                    nextBookings.put(itemId, booking);
                }
            }
        }

        for (Item userItem : userItems) {
            ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(userItem);

            Booking lastBooking = pastBookings.getOrDefault(userItem.getId(), null);
            if (lastBooking != null) {
                itemDtoWithBookings.setLastBooking(BookingMapper.toBookingShort(lastBooking));
            } else {
                itemDtoWithBookings.setLastBooking(null);
            }

            Booking nextBooking = nextBookings.getOrDefault(userItem.getId(), null);
            if (nextBooking != null) {
                itemDtoWithBookings.setNextBooking(BookingMapper.toBookingShort(nextBooking));
            } else {
                itemDtoWithBookings.setNextBooking(null);
            }

            userItemsDto.add(setCommentsToItemDto(itemDtoWithBookings));
        }

        return userItemsDto;
    }

    public List<ItemDto> searchByText(String text, long from, int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Pageable validation error");
        }

        int page = (int) (from / size);
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        Pageable pageable = PageRequest.of(page, size);

        List<Item> itemList = itemRepository.findAll(pageable).toList();
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
