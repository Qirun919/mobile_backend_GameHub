package com.example.GamesHubMobileBackend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "community_servers")
public class CommunityServer {
    @Id
    private String id;
    private String name;
    private String description;
    private List<String> adminIds;
    private List<String> userIds;
    private List<User> admins;
    private List<User> users;
}

