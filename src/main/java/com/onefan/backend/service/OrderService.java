package com.onefan.backend.service;

import com.onefan.backend.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    Optional<Order> findById(Long id);

    List<Order> findAllByBuyerId(Long buyerId);
    Order create(Order order);
}
