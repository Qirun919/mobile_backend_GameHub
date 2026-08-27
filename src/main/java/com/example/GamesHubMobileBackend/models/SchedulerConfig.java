package com.example.GamesHubMobileBackend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "scheduler_config")
public class SchedulerConfig {
    @Id
    private String id;
    private String schedulerName;      // many scheduler
    private int lastProcessedIndex;    // how many i do
}