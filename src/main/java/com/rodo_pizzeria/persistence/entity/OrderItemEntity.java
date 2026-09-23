package com.rodo_pizzeria.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Line of an order, identified by {@code (idOrder, idItem)}.
 * <p>
 * Foreign keys are written through the scalar {@code idOrder}/{@code idPizza} fields; the
 * {@code pizza} and {@code order} associations are read-only views of the same columns.
 */
@Entity
@Table(name = "order_item")
@IdClass(OrderItemId.class)
@Setter
@Getter
@NoArgsConstructor
public class OrderItemEntity {

    @Id
    @Column(name = "id_item", nullable = false)
    private Integer idItem;

    @Id
    @Column(name = "id_order", nullable = false)
    private Integer idOrder;

    @Column(name = "id_pizza", nullable = false)
    private Integer idPizza;

    @Column(nullable = false, columnDefinition = "DECIMAL(2,1)")
    private Double quantity;

    @Column(nullable = false, columnDefinition = "DECIMAL(5,2)")
    private Double price;

    @OneToOne()
    @JoinColumn(name = "id_pizza", referencedColumnName = "id_pizza", insertable = false, updatable = false)
    private PizzaEntity pizza;

    @ManyToOne()
    @JoinColumn(name = "id_order", referencedColumnName = "id_order", insertable = false, updatable = false)
    @JsonIgnore
    private OrderEntity order;

}
