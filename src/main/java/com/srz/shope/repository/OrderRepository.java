package com.srz.shope.repository;

import com.srz.shope.model.Order;
import com.srz.shope.model.UserAccount;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public synchronized Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGen.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public List<Order> findAllByUser(UserAccount user) {
        return new ArrayList<>(orders.values());
    }

    public Order findById(Long id) {
        return orders.get(id);
    }
}