package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequestorId(Integer requestorId, Sort sort);

    @Query("select ir from ItemRequest as ir " +
            "where ir.requestor.id <> ?1 " +
            "order by ir.createdDate asc")
    Page<ItemRequest> findOtherRequestsByRequestorId(Integer requestorId, Pageable pageable);
}
