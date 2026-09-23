package com.rodo_pizzeria.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key of {@link OrderItemEntity}, mapped with {@code @IdClass}.
 * Field names and types must match the entity's {@code @Id} fields, and JPA requires
 * {@link Serializable} plus value-based {@code equals}/{@code hashCode}.
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderItemId implements Serializable {
    private Integer idOrder;
    private Integer idItem;

    @Override
    public int hashCode() {
        return Objects.hash(idOrder, idItem);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OrderItemId that)) return false;
        return Objects.equals(idOrder, that.idOrder) && Objects.equals(idItem, that.idItem);
    }
}
