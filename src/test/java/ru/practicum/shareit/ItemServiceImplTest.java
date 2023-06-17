package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    User user1 = new User(1, "User1", "email@email.com");
    User user2 = new User(2, "User2", "email2@email.com");
    Item item1 = new Item(1, "Название1", "Описание1", true, user1, 0);
    Item item2 = new Item(2, "Название2", "Описание2", true, user2, 0);
    BookingDto bookingDto = new BookingDto(1L,
            LocalDateTime.of(2025, 5, 1, 12, 0),
            LocalDateTime.of(2025, 6, 1, 12, 0));
    Booking booking = new Booking(1,
            LocalDateTime.of(2025, 5, 1, 12, 0),
            LocalDateTime.of(2025, 6, 1, 12, 0),
            item2,
            user1,
            Status.WAITING);
    ItemRequest itemRequest = new ItemRequest();
    ItemDto itemDto = new ItemDto(0, "Название2", "Описание2", true, 1L);

    @Test
    void addItemWhenValidOwnerAndItemThenSave() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.save(ItemMapper.toItem(itemDto, user1)))
                .thenReturn(ItemMapper.toItem(itemDto, user1));

        ItemDto returnedItem = itemService.addItem(itemDto, 1L);

        assertEquals(itemDto, returnedItem);
    }

    @Test
    void updateItemWhenValidItemAndUserAndItemThenSave() {
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item1));
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when((itemRequestRepository.findById(Mockito.anyLong())))
                .thenReturn(Optional.empty());
        Mockito.when(itemRepository.save(item1))
                .thenReturn(item1);

        ItemDto returnedItemDto = itemService.updateItem(ItemMapper.toItemDto(item1), 1L, 1L);

        assertEquals(ItemMapper.toItemDto(item1), returnedItemDto);
    }

    @Test
    void findItemByIdWhenValidItemAndUserThenReturnItemDtoWithBookings() {
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item1));
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when(commentRepository.findByItemId(1L))
                .thenReturn(new ArrayList<>());

        ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(item1);
        itemDtoWithBookings.setComments(new ArrayList<>());

        assertEquals(itemDtoWithBookings, itemService.findItemById(1L, 1L));
    }
}
