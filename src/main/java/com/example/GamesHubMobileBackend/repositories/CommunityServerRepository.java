package com.example.GamesHubMobileBackend.repositories;

import com.example.GamesHubMobileBackend.models.CommunityServer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface CommunityServerRepository extends MongoRepository<CommunityServer, String> {
}

