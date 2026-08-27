package com.example.GamesHubMobileBackend.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnlineStatusMessage {
    private String userId;
    private boolean online;
}
