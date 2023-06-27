package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.CommentDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final String ownerIdHeaderTitle = "x-sharer-user-id";

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody ItemDto itemDto,
                                          @RequestHeader(ownerIdHeaderTitle) long owner) {
        log.info("Adding new item by owner: {}", owner);
        return itemClient.addItem(itemDto, owner);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable long itemId,
                              @RequestHeader(ownerIdHeaderTitle) long owner) {
        log.info("Updating item id: {}", itemId);
        return itemClient.updateItem(itemDto, owner, itemId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable long itemId,
                                            @RequestHeader(ownerIdHeaderTitle) long userId) {
        log.info("Getting item id: {}", itemId);
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwnerId(@RequestHeader(ownerIdHeaderTitle) long owner,
                                                      @RequestParam(required = false, defaultValue = "0") long from,
                                                      @RequestParam(required = false, defaultValue = "500") int size) {
        log.info("Getting all items by owner id: {}",owner);
        return itemClient.findAllByOwnerId(owner, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestParam String text,
                                      @RequestParam(required = false, defaultValue = "0") long from,
                                      @RequestParam(required = false, defaultValue = "500") int size) {
        log.info("Searching item by text: \"{}\"", text);
        return itemClient.searchByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                 @RequestHeader(ownerIdHeaderTitle) long userId,
                                 @RequestBody CommentDto comment) {
        log.info("Adding comment from user id: {} to item id: {}", userId, itemId);
        return itemClient.addComment(itemId, userId, comment);
    }
}
