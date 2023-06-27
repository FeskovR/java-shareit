package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoWithItems findItemRequestById(long id, long requesstorId);

    ItemRequest addItemRequest(ItemRequestDto itemRequestDto, long requestorId);

    List<ItemRequestDtoWithItems> findAllUserRequests(long requestorId);

    List<ItemRequestDtoWithItems> findAllRequests(long requestorId, long from, long size);
}
