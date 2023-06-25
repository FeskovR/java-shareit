package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.CommentDto;
import ru.practicum.shareit.util.ItemValidationService;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> addItem(ItemDto itemDto, long owner) {
        ItemValidationService.validate(itemDto);
        return post("", owner, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, long owner, long itemId) {
        return patch("/" + itemId, owner, itemDto);
    }

    public ResponseEntity<Object> findItemById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> findAllByOwnerId(long owner, long from, int size) {
        return get("", owner);
    }

    public ResponseEntity<Object> searchByText(String text, long from, int size) {
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> addComment(long itemId, long userId, CommentDto comment) {
        ItemValidationService.checkComment(comment);
        return post("/" + itemId + "/comment", userId, comment);
    }
}
