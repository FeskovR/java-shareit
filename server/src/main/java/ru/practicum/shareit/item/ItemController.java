package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final String ownerIdHeaderTitle = "x-sharer-user-id";

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader(ownerIdHeaderTitle) long owner) {
        log.info("Adding new item by owner: {}", owner);
        return itemService.addItem(itemDto, owner);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                           @PathVariable long itemId,
                           @RequestHeader(ownerIdHeaderTitle) long owner) {
        log.info("Updating item id: {}", itemId);
        return itemService.updateItem(itemDto, owner, itemId);
    }

    @GetMapping("{itemId}")
    public ItemDtoWithBookings findItemById(@PathVariable long itemId,
                                            @RequestHeader(ownerIdHeaderTitle) long userId) {
        log.info("Getting item id: {}", itemId);
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBookings> findAllByOwnerId(@RequestHeader(ownerIdHeaderTitle) long owner,
                                                      @RequestParam(required = false, defaultValue = "0") long from,
                                                      @RequestParam(required = false, defaultValue = "500") int size) {
        log.info("Getting all items by owner id: {}",owner);
        return itemService.findAllByOwnerId(owner, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text,
                                      @RequestParam(required = false, defaultValue = "0") long from,
                                      @RequestParam(required = false, defaultValue = "500") int size) {
        log.info("Searching item by text: \"{}\"", text);
        return itemService.searchByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                              @RequestHeader(ownerIdHeaderTitle) long userId,
                              @RequestBody CommentDto comment) {
        log.info("Adding comment from user id: {} to item id: {}", userId, itemId);
        return itemService.addComment(itemId, userId, comment);
    }
}
