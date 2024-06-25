package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdAndEndDateIsBefore(Integer bookerId, LocalDate endDate, Sort sort);

    List<Booking> findAllByBookerIdAndEndDateIsAfter(Integer bookerId, LocalDate endDate, Sort sort);
}
