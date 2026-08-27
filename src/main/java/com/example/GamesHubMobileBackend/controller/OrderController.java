package com.example.GamesHubMobileBackend.controller;

import com.example.GamesHubMobileBackend.models.Order;
import com.example.GamesHubMobileBackend.services.OrderService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity getOrders() {
        var currentOrders = orderService.getOrders();
        if (CollectionUtils.isEmpty(currentOrders)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentOrders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body("Order Not Found.");
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/orders/user/{userId}")
    public ResponseEntity getOrdersByUser(@PathVariable String userId) {
        var orders = orderService.getOrdersByUserId(userId);
        if (CollectionUtils.isEmpty(orders)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/user/{userId}/games")
    public ResponseEntity<Object> getOwnedGames(@PathVariable String userId) {
        var games = orderService.getOwnedGames(userId);
        if (CollectionUtils.isEmpty(games)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(games);
    }

    @PostMapping("/orders")
    public ResponseEntity<Object> addOrder(@RequestBody Order order) {
        if (order.getGameId() == null || order.getGameId().isEmpty()) {
            return ResponseEntity.badRequest().body("Order must contain at least one game.");
        }

        Order saved = orderService.saveOrder(order);

        if (saved == null) {
            return ResponseEntity.badRequest().body("Game does not exist or already owned.");
        }

        return ResponseEntity.ok(saved);
    }


    @PutMapping("/orders/{id}")
    public ResponseEntity<Object> updateOrder(@PathVariable String id, @RequestBody Order order) {
        if (order.getGameId() == null || order.getGameId().isEmpty()) {
            return ResponseEntity.badRequest().body("Order must contain at least one game.");
        }

        Order updated = orderService.updateOrder(id, order);
        if (updated == null) {
            return ResponseEntity.badRequest().body("Order Not Found, or one or more games do not exist.");
        }

        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Object> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order Have Been Deleted");
    }

    // payment
    @PostMapping("/orders/{id}/payment")
    public ResponseEntity<Object> createPayment(@PathVariable String id) {
        try {
            Order updated = orderService.createPayment(id);
            if (updated == null) {
                return ResponseEntity.badRequest().body("Order Not Found.");
            }
            return ResponseEntity.ok(updated);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Payment failed: " + e.getMessage());
        }
    }

    // successful pay
    @GetMapping("/orders/{id}/confirm")
    public ResponseEntity<Object> confirmPayment(@PathVariable String id) {
        Order updated = orderService.confirmPayment(id);
        if (updated == null) {
            return ResponseEntity.badRequest().body("Order Not Found.");
        }
        return ResponseEntity.ok(updated);
    }

    // todo: need an cancel api

    // cancel
    @GetMapping("/orders/{id}/cancel")
    public ResponseEntity<Object> cancelPayment(@PathVariable String id) {
        Order updated = orderService.cancelPayment(id);
        if (updated == null) {
            return ResponseEntity.badRequest().body("Order Not Found.");
        }
        return ResponseEntity.ok(updated);
    }
}