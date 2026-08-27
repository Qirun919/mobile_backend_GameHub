package com.example.GamesHubMobileBackend.repositories;

import com.example.GamesHubMobileBackend.models.SchedulerConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SchedulerConfigRepository extends MongoRepository<SchedulerConfig, String> {
    SchedulerConfig findBySchedulerName(String schedulerName);
}
