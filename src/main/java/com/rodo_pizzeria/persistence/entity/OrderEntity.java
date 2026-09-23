package com.rodo_pizzeria.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order header. Items are loaded EAGER (sorted by price) because they are always part of
 * the response; the customer stays LAZY and is excluded from JSON with {@code @JsonIgnore}.
 */
@Entity
@Table(name = "pizza_order")
@Setter
@Getter
@NoArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order",  unique = true, nullable = false)
    private Integer idOrder;

    @Column(name = "id_customer", nullable = false, length = 15)
    private String idCustomer;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime date;

    @Column(nullable = false, columnDefinition = "DECIMAL(5,2)")
    private Double total;

    /** Order method: {@code D} delivery, {@code C} carryout, {@code S} on site. */
    @Column(nullable = false, columnDefinition = "CHAR(1)")
    private String method;

    @Column(name = "additional_notes", nullable = true, length = 200)
    private String additionalNotes;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    @OrderBy("price ASC")
    private List<OrderItemEntity> items;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer", referencedColumnName = "id_customer", insertable = false, updatable = false)
    @JsonIgnore
    private CustomerEntity customer;

}
