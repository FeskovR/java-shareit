package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;

    Pageable pageable = PageRequest.of(0, 20);

    @BeforeEach
    void addItem() {
        User owner = new User(1L, "Owner", "zz@zz.zz");
        userRepository.save(owner);
        User user = new User(2L, "User", "xx@xx.ru");
        userRepository.save(user);
        ItemRequest request = new ItemRequest(1L, "desc", user, LocalDateTime.now());
        requestRepository.save(request);
        Item item = new Item(1L, "item", "desc", true, owner, 1L);
        itemRepository.save(item);
    }

    @Test
    void findAll() {
        List<Item> returned = itemRepository.findAll(pageable).toList();

        assertEquals(1, returned.size());
        assertEquals("item", returned.get(0).getName());
    }
//
//    @Test
//    void findAllByOwnerId() {
//        List<Item> returned = itemRepository.findAllByOwnerId(1L, pageable);
//
//        assertEquals(1, returned.size());
//        assertEquals("item", returned.get(0).getName());
//    }
//
//    @Test
//    void findAllByRequestId() {
//        List<Item> returned = itemRepository.findAllByRequestId(1L);
//
//        assertEquals(1L, returned.get(0).getRequestId());
//    }
//
//    @Test
//    void findAllByRequestIdIn() {
//    }

    @AfterEach
    void deleteAll() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }
}