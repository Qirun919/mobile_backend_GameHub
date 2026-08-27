package com.example.GamesHubMobileBackend.ResponseModels;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SteamAppListItem {
    private int appid;
    private String name;
    private long last_modified;
    private long price_change_number;
}
