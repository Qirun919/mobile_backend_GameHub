package com.example.GamesHubMobileBackend.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Platforms {
    private boolean windows;
    private boolean mac;
    private boolean linux;
}