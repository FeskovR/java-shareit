package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidationService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemValidationService itemValidationService;
    private final UserValidationService userValidationService;

    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        itemValidationService.validate(itemDto);
        userValidationService.checkUserIsExist(ownerId);
        Item item = ItemMapper.toItem(itemDto, userRepository.findById(ownerId).get());
        Item returnedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(returnedItem);
    }

    public ItemDto updateItem(ItemDto itemDto, long ownerId, long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        Optional<User> optionalUser = userRepository.findById(ownerId);
        if (optionalItem.isEmpty() || optionalUser.isEmpty()) {
            throw new NotFoundException("Item or User not found");
        } else {
            Item updatingItem = optionalItem.get();
            Item newItem = ItemMapper.toItem(itemDto, optionalUser.get());

            if (updatingItem.getOwner() != newItem.getOwner()) {
                throw new NotFoundException("User is not owner of this item");
            }

            if (newItem.getName() == null)
                newItem.setName(updatingItem.getName());
            if (newItem.getDescription() == null)
                newItem.setDescription(updatingItem.getDescription());
            if (newItem.getAvailable() == null)
                newItem.setAvailable(updatingItem.getAvailable());

            newItem.setId(updatingItem.getId());
            Item updatedItem = itemRepository.save(newItem);
            return ItemMapper.toItemDto(updatedItem);
        }
    }

    public ItemDto findItemById(long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);

        if (optionalItem.isPresent()) {
            return ItemMapper.toItemDto(optionalItem.get());
        } else {
            throw new NotFoundException("Item not found");
        }
    }

    public List<ItemDto> findAllByOwnerId(long ownerId) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException("User not found");
        }

        List<Item> userItems = itemRepository.findAllByOwnerId(ownerId);
        List<ItemDto> userItemsDto = new ArrayList<>();

        for (Item userItem : userItems) {
            userItemsDto.add(ItemMapper.toItemDto(userItem));
        }

        return userItemsDto;
    }

    public List<ItemDto> searchByText(String text) {
        List<Item> itemList = itemRepository.findAll();
        List<ItemDto> resultList = new ArrayList<>();

        if (text == null || text.isBlank() || text.isEmpty())
            return resultList;

        for (Item item : itemList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                item.getAvailable()) {
                resultList.add(ItemMapper.toItemDto(item));
            }
        }

        return resultList;
    }
}
