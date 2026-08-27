package com.example.GamesHubMobileBackend.RequestModels;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestModel {
    private String email;
    private String password;
}
