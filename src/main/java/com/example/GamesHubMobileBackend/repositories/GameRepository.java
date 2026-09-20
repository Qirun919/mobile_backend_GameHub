package com.example.GamesHubMobileBackend.repositories;

import com.example.GamesHubMobileBackend.models.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {
    Game findBySteamGameId(int steamGameId);
    List<Game> findByGenresDescription(String description);
    List<Game> findByTitleContainingIgnoreCase(String title);
}