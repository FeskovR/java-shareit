package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private final String ownerIdHeaderTitle = "x-sharer-user-id";

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestBody RequestDto itemRequestDto,
                                                 @RequestHeader(ownerIdHeaderTitle) long requestorId) {
        log.info("Adding new item request by user id {}", requestorId);
        return requestClient.addItemRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUserRequests(@RequestHeader(ownerIdHeaderTitle) long requestorId) {
        log.info("Getting all user id: {} requests", requestorId);
        return requestClient.findAllUserRequests(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests(@RequestHeader(ownerIdHeaderTitle) long requestorId,
                                                         @RequestParam(required = false, defaultValue = "0") long from,
                                                         @RequestParam(required = false, defaultValue = "20") long size) {
        log.info("Getting {} requests from id: {}", size, from);
        return requestClient.findAllRequests(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(@PathVariable long requestId,
                                                   @RequestHeader(ownerIdHeaderTitle) long requestorId) {
        log.info("Getting request id {}", requestId);
        return requestClient.findItemRequestById(requestId, requestorId);
    }
}
