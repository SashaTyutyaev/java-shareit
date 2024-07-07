package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("select b from Booking b " +
            "where ?2 between b.startDate and b.endDate " +
            "and b.booker.id = ?1")
    List<Booking> findAllByBookerIdCurrent(Integer bookerId, LocalDateTime now, Sort sort);

    List<Booking> findAllByBookerIdAndEndDateIsBefore(Integer bookerId, LocalDateTime endDate, Sort sort);

    List<Booking> findAllByBookerIdAndStartDateIsAfter(Integer bookerId, LocalDateTime startDate, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Integer bookerId, Status status, Sort sort);

    List<Booking> findAllByBookerId(Integer bookerId, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "order by b.startDate desc ")
    List<Booking> findAllByItemOwner(Integer ownerId, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and ?2 between b.startDate and b.endDate " +
            "order by b.startDate desc ")
    List<Booking> findAllCurrentBookingByOwnerId(Integer ownerId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndDateIsBefore(Integer ownerId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartDateIsAfter(Integer ownerId, LocalDateTime now, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Integer ownerId, Status status, Sort sort);

    List<Booking> findAllByItemId(Integer itemId, Sort sort);
}
