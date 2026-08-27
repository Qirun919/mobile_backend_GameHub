package com.example.GamesHubMobileBackend.services;

import com.example.GamesHubMobileBackend.models.Game;
import com.example.GamesHubMobileBackend.models.Order;
import com.example.GamesHubMobileBackend.models.SchedulerConfig;
import com.example.GamesHubMobileBackend.repositories.GameRepository;
import com.example.GamesHubMobileBackend.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final GameRepository gameRepository;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, GameRepository gameRepository, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.gameRepository = gameRepository;
        this.paymentService = paymentService;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id).orElse(null);
    }


    public Order saveOrder(Order order) {
        double total = 0;

        for (String gameId : order.getGameId()) {
            Game g = gameRepository.findById(gameId).orElse(null);
            if (g == null) {
                return null;
            }
            for (Order existingOrder : orderRepository.findByUserId(order.getUserId())) {
                if (existingOrder.getGameId().contains(gameId)) {
                    return null;
                }
            }
            total += g.getPrice();
        }

        order.setTotalPrice(total);
        order.setPaymentStatus("idle");
        return orderRepository.save(order);
    }


    public Order updateOrder(String id, Order order) {
        Order existing = orderRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        for (String gameId : order.getGameId()) {
            Game g = gameRepository.findById(gameId).orElse(null);
            if (g == null) {
                return null;
            }
        }

        double total = 0;
        for (String gameId : order.getGameId()) {
            Game g = gameRepository.findById(gameId).orElse(null);
            total += g.getPrice();
        }

        existing.setGameId(order.getGameId());
        existing.setTotalPrice(total);
        return orderRepository.save(existing);
    }

    public List<Game> getOwnedGames(String userId) {
        List<Order> userOrders = orderRepository.findByUserId(userId);
        List<String> gameIds = new ArrayList<>();
        for (Order order : userOrders) {
            for (String gameId : order.getGameId()) {
                if (!gameIds.contains(gameId)) {
                    gameIds.add(gameId);
                }
            }
        }

        List<Game> games = new ArrayList<>();
        for (String gameId : gameIds) {
            Game g = gameRepository.findById(gameId).orElse(null);
            if (g != null) {
                games.add(g);
            }
        }
        return games;
    }

    public boolean deleteOrder(String id) {
        orderRepository.deleteById(id);
        return true;
    }

    // cancel
    public Order cancelPayment(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        order.setPaymentStatus("fail");
        return orderRepository.save(order);
    }


    // create payment
    public Order createPayment(String orderId) throws StripeException {
        // todo: status when order first come in is idle, then during want to make payment is pending_payment,
        //  after payment then is fail or success
        // todo: during coming to here, set payment status to pending_payment
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return null;
            }
            Session session = paymentService.createCheckoutSession(order.getTotalPrice(), "myr", orderId);
            order.setStripePaymentIntentId(session.getId());
            order.setCheckoutUrl(session.getUrl());
        order.setPaymentStatus("pending");
        return orderRepository.save(order);
    }

    // comfirm payment
    public Order confirmPayment(String orderId) {
        // todo: order change to success status
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        order.setPaymentStatus("paid");
        return orderRepository.save(order);
    }
}
