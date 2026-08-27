package com.example.GamesHubMobileBackend.ResponseModels;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SteamApp {
    private List<SteamAppListItem> apps;
}
