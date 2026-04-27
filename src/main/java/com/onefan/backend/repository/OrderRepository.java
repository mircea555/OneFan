package com.onefan.backend.repository;

import com.onefan.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByBuyerId(Long buyerId);
}
