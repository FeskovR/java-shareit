package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId);

    List<Item> findAllByRequestId(long requestId);

    @Query("select i from Item as i " +
            "where request_id in (?1)")
    List<Item> findAllByRequestIdIn(Set<Long> requestsId);
}
