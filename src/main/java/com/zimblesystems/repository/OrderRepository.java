package com.zimblesystems.repository;

import com.zimblesystems.entity.Order;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepository implements ReactivePanacheMongoRepository<Order> {
}
