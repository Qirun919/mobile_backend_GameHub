package com.example.GamesHubMobileBackend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "steam_games")
public class SteamGame {
    @Id
    private String id;

    @Indexed(unique = true)
    private int appid;

    private String name;

    @Indexed
    private boolean processed;
}
