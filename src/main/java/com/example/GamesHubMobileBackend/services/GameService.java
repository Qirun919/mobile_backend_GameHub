package com.example.GamesHubMobileBackend.services;

import com.example.GamesHubMobileBackend.models.Game;
import com.example.GamesHubMobileBackend.models.Order;
import com.example.GamesHubMobileBackend.repositories.GameRepository;
import com.example.GamesHubMobileBackend.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Arrays.stream;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final OrderRepository orderRepository;

    public GameService(GameRepository gameRepository, OrderRepository orderRepository) {
        this.gameRepository = gameRepository;
        this.orderRepository = orderRepository;
    }

    public List<Game> getGames() {
        return gameRepository.findAll();
    }

    public Game getGameById(String id) {
        return gameRepository.findById(id).orElse(null);
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    public Game updateGame(Game game) {
        Game existing = gameRepository.findById(game.getId()).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setTitle(game.getTitle());
        existing.setDescription(game.getDescription());
        existing.setCoverImage(game.getCoverImage());
        existing.setTrailerUrl(game.getTrailerUrl());
        existing.setPrice(game.getPrice());
        return existing;
    }

    public boolean deleteGame(String id) {
        gameRepository.deleteById(id);
        return true;
    }

    public List<Game> getPopularGames(int limit) {
        List<Order> allOrders = orderRepository.findAll();
        Map<String, Integer> purchaseCount = new HashMap<>();

        for (Order order : allOrders) {
            for (String gameId : order.getGameId()) {
                purchaseCount.merge(gameId, 1, Integer::sum);
            }
        }

        List<Game> allGames = gameRepository.findAll();
        allGames.sort((a, b) -> {
            int countA = purchaseCount.getOrDefault(a.getId(), 0);
            int countB = purchaseCount.getOrDefault(b.getId(), 0);
            return countB - countA;
        });

        return allGames.subList(0, Math.min(limit, allGames.size()));
    }

    public List<Game> getGamesPaged(int page, int size) {
        List<Game> allGames = gameRepository.findAll();

        Collections.reverse(allGames);
        
        int start = page * size;
        if (start >= allGames.size()) {
            return new java.util.ArrayList<>();
        }
        int end = Math.min(start + size, allGames.size());
        return allGames.subList(start, end);
    }
}


