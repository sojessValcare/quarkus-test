package com.zimblesystems.services;

import com.zimblesystems.dto.OrderRequest;
import com.zimblesystems.dto.OrderResponse;
import com.zimblesystems.entity.Order;
import com.zimblesystems.repository.OrderRepository;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;

    public Uni<OrderResponse> createOrder(OrderRequest orderRequest) {

        Log.info("Creating order for product id: " + orderRequest.getProductId());
        Order orderEntity = new Order();

        orderEntity.setProductId(orderRequest.getProductId());
        orderEntity.setQuantity(orderRequest.getQuantity());

        return orderRepository.persist(orderEntity).onItem()
                .transform(order -> {
                    OrderResponse response = new OrderResponse();
                    Log.info("Order Id :" + order.getId());
                    response.setOrderId(order.getId().toString());
                    response.setStatus("CREATED");

                    return response;
                }).onItem()
                .ifNull()
                .continueWith(() -> {
                    OrderResponse response = new OrderResponse();
                    response.setStatus("FAILED");
                    return response;
                }).onFailure().recoverWithItem(e -> {

                    Log.error("Error in creating order e: " + e.getMessage());
                    OrderResponse response = new OrderResponse();
                    response.setStatus("FAILED");
                    return response;
                });

    }

    public Uni<List<OrderResponse>> getAllOrders() {

        return orderRepository.findAll().list()
                .onItem()
                .ifNotNull()
                .transform(orders -> {

                    return orders.stream()
                            .map(order -> {
                                OrderResponse response = new OrderResponse();
                                response.setOrderId(order.getId().toString());
                                response.setStatus("FETCHED");
                                return response;
                            })
                            .toList();
                })
                .onItem()
                .ifNull().continueWith(ArrayList::new)
                .onFailure().recoverWithItem( ex -> {
                    Log.error("Error fetching order details e: " + ex.getMessage());
                    return new ArrayList<>();
                });
    }
}
