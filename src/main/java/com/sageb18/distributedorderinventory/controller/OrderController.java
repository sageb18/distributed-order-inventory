package com.sageb18.distributedorderinventory.controller;

import com.sageb18.distributedorderinventory.model.Order;
import com.sageb18.distributedorderinventory.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Collection<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable String orderId) {
        return orderService.getOrderById(orderId);
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }
}
