package com.onefan.backend.controller;

import com.onefan.backend.model.Collectible;
import com.onefan.backend.model.Order;
import com.onefan.backend.model.OrderItem;
import com.onefan.backend.payload.request.OrderRequest;
import com.onefan.backend.repository.CollectibleRepository;
import com.onefan.backend.security.UserDetailsImpl;
import com.onefan.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/buyer/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CollectibleRepository collectibleRepository;

    @GetMapping
    public ResponseEntity<List<Order>> listOrdersForBuyer(Authentication authentication) {
        Long buyerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        List<Order> orders = orderService.findAllByBuyerId(buyerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderDetail(
            @PathVariable Long orderId,
            Authentication authentication) {

        Long buyerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        Optional<Order> maybeOrder = orderService.findById(orderId);
        if (maybeOrder.isEmpty()) {
            return ResponseEntity.<Order>notFound().build();
        }

        Order order = maybeOrder.get();
        if (!order.getBuyerId().equals(buyerId)) {
            return ResponseEntity.<Order>status(403).build();
        }

        return ResponseEntity.ok(order);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            Authentication authentication) {

        Long buyerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setPlacedAt(Instant.now());

        double total = 0.0;

        for (OrderRequest.Item reqItem : orderRequest.getItems()) {
            Long collectibleId = reqItem.getCollectibleId();
            int qty = reqItem.getQuantity();

            Optional<Collectible> maybeCollectible = collectibleRepository.findById(collectibleId);
            if (maybeCollectible.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(null);
            }

            Collectible col = maybeCollectible.get();
            double unitPrice = col.getPrice();
            total += unitPrice * qty;

            OrderItem item = new OrderItem(collectibleId, unitPrice, qty);
            order.addItem(item);
        }

        order.setTotalPrice(total);

        Order saved = orderService.create(order);

        URI location = URI.create("/api/buyer/orders/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }


    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        Long buyerId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        Optional<Order> maybeOrder = orderService.findById(orderId);
        if (maybeOrder.isEmpty()) {
            return ResponseEntity.<Void>notFound().build();
        }

        Order order = maybeOrder.get();
        if (!order.getBuyerId().equals(buyerId)) {
            return ResponseEntity.<Void>status(403).build();
        }

        if (order.getStatus() != Order.Status.PENDING) {
            return ResponseEntity.<Void>status(409).build();
        }

        order.setStatus(Order.Status.CANCELED);
        orderService.create(order);

        return ResponseEntity.noContent().build();
    }
}
