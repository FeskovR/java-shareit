package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.util.ItemRequestValidationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDtoWithItems findItemRequestById(long id, long requestorId) {
        User requestor = userRepository.findById(requestorId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Request not found")
        );
        List<Item> items = itemRepository.findAllByRequestId(id);

        return ItemRequestMapper.toItemRequestDtoWithItems(itemRequest, items);
    }

    @Override
    public ItemRequest addItemRequest(ItemRequestDto itemRequestDto, long requestorId) {
        ItemRequestValidationService.checkItemRequest(itemRequestDto);
        User requestor = userRepository.findById(requestorId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor, LocalDateTime.now());

        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestDtoWithItems> findAllUserRequests(long requestorId) {
        User requestor = userRepository.findById(requestorId).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(requestorId);
        Set<Long> requestsId = new HashSet<>();
        List<ItemRequestDtoWithItems> result = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            requestsId.add(itemRequest.getId());
        }
        List<Item> allItemsByRequestor = itemRepository.findAllByRequestIdIn(requestsId);

        for (ItemRequest itemRequest : itemRequests) {
            List<Item> itemsByRequest = new ArrayList<>();
            for (Item item : allItemsByRequestor) {
                if (item.getRequestId() == itemRequest.getId()) {
                    itemsByRequest.add(item);
                }
            }

            ItemRequestDtoWithItems itemRequestDtoWithItems = ItemRequestMapper.toItemRequestDtoWithItems(itemRequest,
                    itemsByRequest);
            result.add(itemRequestDtoWithItems);
        }

        return result;
    }

    @Override
    public List<ItemRequestDtoWithItems> findAllRequests(long requestorId, long from, long size) {
        User requestor = userRepository.findById(requestorId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        if (from < 0 || size < 1) {
            throw new ValidationException("Pagination error");
        }
        int page = (int) (from / size);
        int sizeOfPage = (int) size;
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable pageable = PageRequest.of(page, sizeOfPage, sort);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAllByRequestorIdNotNull(pageable);
        Set<Long> requestsId = new HashSet<>();
        List<ItemRequestDtoWithItems> result = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequestPage) {
            requestsId.add(itemRequest.getId());
        }
        List<Item> allItemsByRequestor = itemRepository.findAllByRequestIdIn(requestsId);

        for (ItemRequest itemRequest : itemRequestPage) {
            List<Item> itemsByRequest = new ArrayList<>();
            for (Item item : allItemsByRequestor) {
                if (item.getRequestId() == itemRequest.getId()) {
                    itemsByRequest.add(item);
                }
            }

            if (itemRequest.getRequestor().getId() == requestorId)
                continue;

            ItemRequestDtoWithItems itemRequestDtoWithItems = ItemRequestMapper.toItemRequestDtoWithItems(itemRequest,
                    itemsByRequest);
            result.add(itemRequestDtoWithItems);
        }

        return result;
    }
}
