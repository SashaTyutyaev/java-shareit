package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description")
    private String description;

}
