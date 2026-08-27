package com.example.GamesHubMobileBackend.repositories;

import com.example.GamesHubMobileBackend.models.SteamGame;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SteamGameRepository extends MongoRepository<SteamGame, String> {
    boolean existsByAppid(int appid);
    List<SteamGame> findByProcessedFalse();
    List<SteamGame> findByProcessedTrue();
}