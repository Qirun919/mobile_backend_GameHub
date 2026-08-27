package com.example.GamesHubMobileBackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "friendships")
public class Friendship {
    @Id
    private String id;
    private String userId;
    private String friendId;
    private String status;
}
