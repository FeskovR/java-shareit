package ru.practicum.shareit.booking;

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
    List<Booking> findAllBookingsByBooker(User booker);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 and b.start < ?2 and b.end > ?2 " +
            "order by b.start desc ")
    List<Booking> findCurrentBookingsByBooker(User booker, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 and b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> findPastBookingsByBooker(User booker, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findFutureBookingsByBooker(User user, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 and b.status = 'WAITING' " +
            "order by b.start desc ")
    List<Booking> findWaitingBookingsByBooker(User user);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc ")
    List<Booking> findRejectedBookingsByBooker(User user);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 " +
            "order by b.start desc ")

    List<Booking> findAllBookingsByOwner(User user);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 and b.start < ?2 and b.end > ?2 " +
            "order by b.start desc ")
    List<Booking> findCurrentBookingsByOwner(User user, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 and b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> findPastBookingsByOwner(User user, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findFutureBookingsByOwner(User user, LocalDateTime now);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 and b.status = 'WAITING' " +
            "order by b.start desc ")
    List<Booking> findWaitingBookingsByOwner(User user);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1 and b.status = 'REJECTED' " +
            "order by b.start desc ")
    List<Booking> findRejectedBookingsByOwner(User user);

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
