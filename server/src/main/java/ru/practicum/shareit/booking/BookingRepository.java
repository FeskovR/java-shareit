package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 " +
            "order by b.start desc ")
    Page<Booking> findAllBookingsByBooker(User booker, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 and b.start < ?2 and b.end > ?2 " +
            "order by b.start asc ")
    List<Booking> findCurrentBookingsByBooker(User booker, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 and b.end < ?2 " +
            "order by b.start desc ")
    Page<Booking> findPastBookingsByBooker(User booker, LocalDateTime now, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 and b.start > ?2 " +
            "order by b.start desc ")
    Page<Booking> findFutureBookingsByBooker(User user, LocalDateTime now, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 and b.status = 'WAITING' " +
            "order by b.start desc ")
    Page<Booking> findWaitingBookingsByBooker(User user, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc ")
    Page<Booking> findRejectedBookingsByBooker(User user, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 " +
            "order by b.start desc ")

    Page<Booking> findAllBookingsByOwner(User user, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 and b.start < ?2 and b.end > ?2 " +
            "order by b.start desc ")
    Page<Booking> findCurrentBookingsByOwner(User user, LocalDateTime now, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 and b.end < ?2 " +
            "order by b.start desc ")
    Page<Booking> findPastBookingsByOwner(User user, LocalDateTime now, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 and b.start > ?2 " +
            "order by b.start desc ")
    Page<Booking> findFutureBookingsByOwner(User user, LocalDateTime now, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 and b.status = 'WAITING' " +
            "order by b.start desc ")
    Page<Booking> findWaitingBookingsByOwner(User user, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc ")
    Page<Booking> findRejectedBookingsByOwner(User user, Pageable pageable);

    @Query("select b from Booking as b " +
            "join b.item i " +
            "where b.item = ?1 " +
            "and b.start > ?2 " +
            "and i.owner = ?3 " +
            "and b.status <> 'REJECTED' " +
            "order by b.start asc ")
    List<Booking> getAllBookingsForNext(Item item, LocalDateTime now, User owner);

    @Query("select b from Booking b " +
            "join b.item i " +
            "where b.item = ?1 " +
            "and b.start < ?2 " +
            "and i.owner = ?3 " +
            "and b.status <> 'REJECTED' " +
            "order by b.start desc ")
    List<Booking> getAllBookingsForLast(Item item, LocalDateTime now, User owner);

    @Query("select count(b) from Booking b " +
            "where b.item = ?1 " +
            "and b.end < ?2 " +
            "and b.booker = ?3 " +
            "and b.status <> 'REJECTED' ")
    Long countListOfBookersForItem(Item item, LocalDateTime now, User user);


}
