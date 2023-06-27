package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String ownerIdHeaderTitle = "x-sharer-user-id";

    @PostMapping
    public ItemRequest addItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                      @RequestHeader(ownerIdHeaderTitle) long requestorId) {
        log.info("Adding new item request by user id {}", requestorId);
        return itemRequestService.addItemRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> findAllUserRequests(@RequestHeader(ownerIdHeaderTitle) long requestorId) {
        log.info("Getting all user id: {} requests", requestorId);
        return itemRequestService.findAllUserRequests(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> findAllRequests(@RequestHeader(ownerIdHeaderTitle) long requestorId,
                                                         @RequestParam(required = false, defaultValue = "0") long from,
                                                         @RequestParam(required = false, defaultValue = "20") long size) {
        log.info("Getting {} requests from id: {}", size, from);
        return itemRequestService.findAllRequests(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems findRequestById(@PathVariable long requestId,
                                                   @RequestHeader(ownerIdHeaderTitle) long requestorId) {
        log.info("Getting request id {}", requestId);
        return itemRequestService.findItemRequestById(requestId, requestorId);
    }
}
