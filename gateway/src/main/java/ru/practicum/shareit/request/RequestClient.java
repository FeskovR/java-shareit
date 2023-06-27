package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.util.ItemRequestValidationService;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItemRequest(RequestDto itemRequestDto, long requestorId) {
        ItemRequestValidationService.checkItemRequest(itemRequestDto);
        return post("", requestorId, itemRequestDto);
    }

    public ResponseEntity<Object> findAllUserRequests(long requestorId) {
        return get("", requestorId);
    }

    public ResponseEntity<Object> findAllRequests(long requestorId, long from, long size) {
        return get("/all?from=" + from + "&size=" + size, requestorId);
    }

    public ResponseEntity<Object> findItemRequestById(long requestId, long requestorId) {
        return get("/" + requestId, requestorId);
    }
}
