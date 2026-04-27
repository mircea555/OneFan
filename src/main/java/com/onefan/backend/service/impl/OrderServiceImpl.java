package com.onefan.backend.service.impl;

import com.onefan.backend.model.Order;
import com.onefan.backend.repository.OrderRepository;
import com.onefan.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAllByBuyerId(Long buyerId) {
        return orderRepository.findAllByBuyerId(buyerId);
    }

    @Override
    public Order create(Order order) {
        return orderRepository.save(order);
    }
}
