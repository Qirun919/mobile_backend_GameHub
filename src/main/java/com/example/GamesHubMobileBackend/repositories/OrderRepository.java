package com.example.GamesHubMobileBackend.repositories;

import com.example.GamesHubMobileBackend.models.Game;
import com.example.GamesHubMobileBackend.models.Order;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
}
