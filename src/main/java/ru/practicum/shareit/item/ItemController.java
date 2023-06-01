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

    /**
     * Добавление новой вещи
     * @param itemDto DTO for item
     * @param owner owner ID
     * @return item DTO
     */
    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader(ownerIdHeaderTitle) long owner) {
        log.info("Adding new item by owner: " + owner);
        return itemService.addItem(itemDto, owner);
    }

    /**
     * Обновление вещи
     * @param itemDto DTO for item
     * @param itemId long item ID
     * @param owner owner ID
     * @return item DTO
     */
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                           @PathVariable long itemId,
                           @RequestHeader(ownerIdHeaderTitle) long owner) {
        log.info("Updating item id: " + itemId);
        return itemService.updateItem(itemDto, owner, itemId);
    }

    /**
     * Получение вещи по ID
     * @param itemId item ID
     * @return item DTO
     */
    @GetMapping("{itemId}")
    public ItemDtoWithBookings findItemById(@PathVariable long itemId,
                                            @RequestHeader(ownerIdHeaderTitle) long userId) {
        log.info("Getting item id: " + itemId);
        return itemService.findItemById(itemId, userId);
    }

    /**
     * Получение всех вещей одного пользователя
     * @param owner owner ID
     * @return list of user itemsDto
     */
    @GetMapping
    public List<ItemDtoWithBookings> findAllByOwnerId(@RequestHeader(ownerIdHeaderTitle) long owner) {
        log.info("Getting all items by owner id: " + owner);
        return itemService.findAllByOwnerId(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        log.info("Searching item by text: \"" + text + "\"");
        return itemService.searchByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                              @RequestHeader(ownerIdHeaderTitle) long userId,
                              @RequestBody CommentDto comment) {
        log.info("Adding comment from user id: " + userId + " to item id: " + itemId);
        return itemService.addComment(itemId, userId, comment);
    }
}
