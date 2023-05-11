package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final String USER_ID_FROM_HEADER = "x-sharer-user-id";

    @PostMapping
    public Item addItem(@RequestBody ItemDto itemDto, @RequestHeader Map<String, String> headers) {
        long ownerId = getOwnerId(headers);
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody ItemDto itemDto,
                           @PathVariable long itemId,
                           @RequestHeader Map<String, String> headers) {
        long ownerId = getOwnerId(headers);
        return itemService.updateItem(itemDto, ownerId, itemId);
    }

    @GetMapping("{itemId}")
    public Item findItemById(@PathVariable long itemId) {
        return itemService.findItemById(itemId);
    }

    @GetMapping
    public List<Item> findAllByOwnerId(@RequestHeader Map<String, String> headers) {
        long ownerId = getOwnerId(headers);
        return itemService.findAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<Item> searchByText(@RequestParam String text) {
        return itemService.searchByText(text);
    }

    private long getOwnerId(Map<String, String> headers) {
        return Long.parseLong(headers.getOrDefault(USER_ID_FROM_HEADER, "0"));
    }
}
