package com.sageb18.distributedorderinventory.service;

import com.sageb18.distributedorderinventory.model.Order;
import com.sageb18.distributedorderinventory.model.OrderStatus;
import com.sageb18.distributedorderinventory.model.Product;
import com.sageb18.distributedorderinventory.repository.OrderRepository;
import com.sageb18.distributedorderinventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(Order order) {

        // checks if product exists
        Product product = productRepository.findById(order.getProductId());

        if (product == null) {
            order.setStatus(OrderStatus.FAILED_PRODUCT_CHECK);
            return orderRepository.save(order);
        }

        // check if order quantity > 0
        if (order.getQuantity() <= 0) {
            order.setStatus(OrderStatus.FAILED_ORDER_QUANTITY);
            return orderRepository.save(order);
        }

        // verify enough stock
        if (product.getStock() < order.getQuantity()) {
            order.setStatus(OrderStatus.FAILED_STOCK_CHECK);
            return orderRepository.save(order);
        }

        /*
        if everything checks out,
        1. reduce stock
        2. confirm order
        3. persist
         */

        // reduce stock
        product.setStock(product.getStock() - order.getQuantity());

        // confirm order
        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrderId(UUID.randomUUID().toString());

        // persist
        return orderRepository.save(order);
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public Collection<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
