package com.rodo_pizzeria.persistence.repository;

import com.rodo_pizzeria.persistence.entity.OrderEntity;
import com.rodo_pizzeria.persistence.projection.OrderSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends ListCrudRepository<OrderEntity, Integer> {
    /** Derived query: orders placed after the given date and time. */
    List<OrderEntity> findByDateAfter(LocalDateTime date);
    /** Derived query: orders whose method is any of the given codes ({@code IN} clause). */
    List<OrderEntity> findAllByMethodIn(List<String> methods);

    /** Native SQL mapped back to the {@link OrderEntity} entity. */
    @Query(value = "SELECT * FROM pizza_order WHERE id_customer = :id", nativeQuery = true)
    List<OrderEntity> findCustomerOrder(@Param("id") String idCustomer);

    /**
     * Native aggregation across four tables; column aliases match the {@link OrderSummary}
     * getters. {@code GROUP_CONCAT} joins the pizza names into a single string.
     */
    @Query(value =
            "SELECT po.id_order AS orderId, cu.name AS customerName, po.date AS orderDate, " +
            "       po.total AS orderTotal, GROUP_CONCAT(pi.name) AS pizzaNames " +
            "FROM pizza_order po " +
            "       INNER JOIN customer cu ON po.id_customer = cu.id_customer " +
            "       INNER JOIN order_item oi ON po.id_order = oi.id_order " +
            "       INNER JOIN pizza pi ON oi.id_pizza = pi.id_pizza " +
            "WHERE po.id_order = :orderId " +
            "GROUP BY po.id_order, cu.name, po.date, po.total", nativeQuery = true)
    OrderSummary findSummary(@Param("orderId") int orderId);

    /**
     * Calls the {@code take_random_pizza_order} stored procedure
     * (see {@code database/01-procedures.sql}) and returns its {@code OUT order_taken} parameter.
     */
    @Procedure(value = "take_random_pizza_order", outputParameterName = "order_taken")
    boolean saveRandomOrder(@Param("id_customer") String idCustomer, @Param("method") String method);
}
