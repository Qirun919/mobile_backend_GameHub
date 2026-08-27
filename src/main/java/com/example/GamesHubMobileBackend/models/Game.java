package com.example.GamesHubMobileBackend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "games")
public class Game {
    @Id
    private String id;
    private int steamGameId;
    private String title;
    private String description;
    private GameImage coverImage;
    private List<GameImage> screenshots;
    private String trailerUrl;
    private double price;
}