package com.rodo_pizzeria.persistence.projection;

import java.time.LocalDateTime;

/**
 * Interface-based projection for {@code OrderRepository#findSummary}. Spring Data creates a
 * proxy whose getters map to the native query's column aliases ({@code orderId},
 * {@code customerName}, ...), so no entity is loaded.
 */
public interface OrderSummary {
    Integer getOrderId();
    String getCustomerName();
    LocalDateTime getOrderDate();
    Double getOrderTotal();
    String getPizzaNames();
}
