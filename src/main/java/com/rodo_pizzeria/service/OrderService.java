package com.rodo_pizzeria.service;

import com.rodo_pizzeria.persistence.entity.OrderEntity;
import com.rodo_pizzeria.persistence.projection.OrderSummary;
import com.rodo_pizzeria.persistence.repository.OrderRepository;
import com.rodo_pizzeria.service.dto.RandomOrderDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    // Values of the pizza_order.method column
    public static final String DELIVERY = "D";
    public static final String CARRYOUT = "C";
    public static final String ON_SITE = "S";

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderEntity> getAll() {
        return this.orderRepository.findAll();
    }

    public List<OrderEntity> getTodayOrders() {
        LocalDateTime today = LocalDate.now().atTime(0,0);
        return this.orderRepository.findByDateAfter(today);
    }

    public List<OrderEntity> getOutsideOrders() {
        List<String> methods = Arrays.asList(DELIVERY, CARRYOUT);
        return this.orderRepository.findAllByMethodIn(methods);
    }

    public OrderEntity save(OrderEntity order) {
        return this.orderRepository.save(order);
    }

    public boolean exist(Integer idOrder) {
        return this.orderRepository.existsById(idOrder);
    }

    public List<OrderEntity> getCustomerOrders(String idCustomer) {
        return this.orderRepository.findCustomerOrder(idCustomer);
    }

    public OrderSummary getSummary(int idOrder) {
        return this.orderRepository.findSummary(idOrder);
    }

    /** Places a discounted order for a random pizza; returns {@code false} if the procedure rolled back. */
    @Transactional
    public boolean saveRandomOrder(RandomOrderDto randomOrderDto) {
        return this.orderRepository.saveRandomOrder(randomOrderDto.getIdCustomer(), randomOrderDto.getMethod());
    }
}
