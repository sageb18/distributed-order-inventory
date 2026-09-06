package com.sageb18.distributedorderinventory.service;

import com.sageb18.distributedorderinventory.model.Order;
import com.sageb18.distributedorderinventory.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        order.setOrderId(UUID.randomUUID().toString());

        orderRepository.save(order);

        return order;
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public Collection<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
