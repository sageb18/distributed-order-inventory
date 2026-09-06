package com.sageb18.distributedorderinventory.dao;

import com.sageb18.distributedorderinventory.model.Order;
import com.sageb18.distributedorderinventory.model.OrderStatus;
import com.sageb18.distributedorderinventory.repository.OrderRepository;
import com.sageb18.distributedorderinventory.repository.ProductRepository;
import com.sageb18.distributedorderinventory.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for OrderService. The OrderRepository is mocked, so nothing here
 * talks to a real (or local) DynamoDB.
 */
@ExtendWith(MockitoExtension.class)
public class JpaOrderDaoTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderAssignsAGeneratedOrderId() {
        Order order = new Order(null, "product-1", 3, OrderStatus.PENDING);

        Order created = orderService.createOrder(order);

        assertThat(created.getOrderId()).isNotBlank();
        assertThatCode(() -> UUID.fromString(created.getOrderId())).doesNotThrowAnyException();
    }

    @Test
    void createOrderOverwritesAnyClientSuppliedOrderId() {
        Order order = new Order("client-supplied-id", "product-1", 3, OrderStatus.PENDING);

        Order created = orderService.createOrder(order);

        assertThat(created.getOrderId()).isNotEqualTo("client-supplied-id");
    }

    @Test
    void createOrderPassesTheOrderToTheRepositoryWithTheRemainingFieldsUntouched() {
        Order order = new Order(null, "product-1", 3, OrderStatus.PENDING);

        Order saved = orderService.createOrder(order);

        assertThat(saved.getProductId()).isEqualTo("product-1");
        assertThat(saved.getQuantity()).isEqualTo(3);
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.FAILED_PRODUCT_CHECK);
        assertThat(saved.getOrderId()).isNotBlank();
    }

    @Test
    void createOrderReturnsWhateverTheRepositoryPersisted() {
        Order created = orderService.createOrder(new Order(null, "product-1", 3, OrderStatus.CONFIRMED));

        ArgumentCaptor<Order> persisted = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(persisted.capture());

        assertThat(created).isSameAs(persisted.getValue());
        assertThat(created.getOrderId()).isEqualTo(persisted.getValue().getOrderId());
        assertThat(created.getProductId()).isEqualTo("product-1");
        assertThat(created.getQuantity()).isEqualTo(3);
        assertThat(created.getStatus()).isEqualTo(OrderStatus.FAILED_PRODUCT_CHECK);
    }

    @Test
    void createOrderGeneratesADistinctOrderIdPerCall() {
        Order first = orderService.createOrder(new Order(null, "product-1", 1, OrderStatus.PENDING));
        Order second = orderService.createOrder(new Order(null, "product-1", 1, OrderStatus.PENDING));

        assertThat(first.getOrderId()).isNotEqualTo(second.getOrderId());
    }

    @Test
    void getOrderByIdReturnsTheOrderFoundByTheRepository() {
        Order order = new Order(null, "product-1", 3, OrderStatus.CONFIRMED);
        Order createdOrder = orderService.createOrder(order);
        // stub with mockito since this is a dummy db
        when(orderRepository.findById(createdOrder.getOrderId())).thenReturn(createdOrder);

        Order found = orderService.getOrderById(createdOrder.getOrderId());

        assertThat(found.getOrderId()).isEqualTo(createdOrder.getOrderId());
        assertThat(found.getProductId()).isEqualTo(order.getProductId());
        assertThat(found.getStatus()).isEqualTo(order.getStatus());
        assertThat(found.getQuantity()).isEqualTo(order.getQuantity());
    }

    @Test
    void getOrderByIdReturnsNullWhenTheOrderDoesNotExist() {
        assertThat(orderService.getOrderById("missing-id")).isNull();
    }

    @Test
    void getAllOrdersReturnsEveryOrderFromTheRepository() {
        Order first = new Order("order-1", "product-1", 1, OrderStatus.PENDING);
        Order second = new Order("order-2", "product-2", 2, OrderStatus.FAILED_ORDER_QUANTITY);
        // stub with mockito since this is a dummy db
        when(orderRepository.findAll()).thenReturn(List.of(first, second));

        Collection<Order> orders = orderService.getAllOrders();

        assertThat(orders).containsExactly(first, second);
    }

    @Test
    void getAllOrdersReturnsAnEmptyCollectionWhenNoOrdersExist() {
        assertThat(orderService.getAllOrders()).isEmpty();
    }
}
