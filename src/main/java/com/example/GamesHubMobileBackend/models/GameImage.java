package com.example.GamesHubMobileBackend.models;

import com.example.GamesHubMobileBackend.enums.ImageType;
import lombok.Data;

@Data
public class GameImage {
    private int id;
    private String name;
    private String url;
    private byte[] content;
    private ImageType type;

}
