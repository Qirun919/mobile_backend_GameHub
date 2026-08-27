package com.example.GamesHubMobileBackend.RequestModels;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddFriendRequestModel {
        private String userId;
        private String friendId;
}
