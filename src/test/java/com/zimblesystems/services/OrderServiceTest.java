package com.zimblesystems.services;

import com.zimblesystems.dto.OrderRequest;
import com.zimblesystems.dto.OrderResponse;
import com.zimblesystems.entity.Order;
import com.zimblesystems.repository.OrderRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class OrderServiceTest {

    @InjectMock
    OrderRepository orderRepository;

    @Inject
    private OrderService orderService;


    @Test
    void testCreateOrder_Success() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId("12345");
        orderRequest.setQuantity(2);

        Order order = new Order();
        ObjectId orderId = new ObjectId();
        order.setId(orderId);
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());

        when(orderRepository.persist(any(Order.class))).thenReturn(Uni.createFrom().item(order));
        // Act
        Uni<OrderResponse> responseUni = orderService.createOrder(orderRequest);
        OrderResponse response = responseUni.await().indefinitely();

        // Assert
        assertEquals("CREATED", response.getStatus());
        assertEquals(orderId.toString(), response.getOrderId());
        verify(orderRepository, times(1)).persist(any(Order.class));
    }

    @Test
    void testCreateOrder_Failure() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId("12345");
        orderRequest.setQuantity(2);

        when(orderRepository.persist(any(Order.class))).thenReturn(Uni.createFrom().failure(new RuntimeException("Database error")));

        // Act
        Uni<OrderResponse> responseUni = orderService.createOrder(orderRequest);
        OrderResponse response = responseUni.await().indefinitely();

        // Assert
        assertEquals("FAILED", response.getStatus());
        verify(orderRepository, times(1)).persist(any(Order.class));
    }


    @Test
    void testGetAllOrders_Success() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        ObjectId order1Id = new ObjectId();
        order1.setId(order1Id);
        order1.setProductId("12345");
        order1.setQuantity(2);

        Order order2 = new Order();
        ObjectId order2Id = new ObjectId();
        order2.setId(order2Id);
        order2.setProductId("67890");
        order2.setQuantity(1);

        orders.add(order1);
        orders.add(order2);

        ReactivePanacheQuery panacheQueryMock = Mockito.mock(ReactivePanacheQuery.class);
        when(orderRepository.findAll()).thenReturn(panacheQueryMock);
        when(panacheQueryMock.list()).thenReturn(Uni.createFrom().item(orders));

        // Act
        Uni<List<OrderResponse>> responseUni = orderService.getAllOrders();
        List<OrderResponse> responses = responseUni.await().indefinitely();

        // Assert
        assertEquals(2, responses.size());
        assertTrue(responses.stream().anyMatch(r -> r.getOrderId().equals(order1Id.toString())));
        assertTrue(responses.stream().anyMatch(r -> r.getOrderId().equals(order2Id.toString())));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetAllOrders_Failure() {
        // Arrange
        ReactivePanacheQuery panacheQueryMock = Mockito.mock(ReactivePanacheQuery.class);
        when(orderRepository.findAll()).thenReturn(panacheQueryMock);
        when(panacheQueryMock.list()).thenReturn(Uni.createFrom().failure(new RuntimeException("Database error")));

        // Act
        Uni<List<OrderResponse>> responseUni = orderService.getAllOrders();
        List<OrderResponse> responses = responseUni.await().indefinitely();

        // Assert
        assertTrue(responses.isEmpty());
        verify(orderRepository, times(1)).findAll();
    }

}
