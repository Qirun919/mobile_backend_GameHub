package com.example.GamesHubMobileBackend.repositories;

import com.example.GamesHubMobileBackend.models.Friendship;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface FriendshipRepository extends MongoRepository<Friendship, String> {
    List<Friendship> findByUserIdOrFriendId(String userId, String friendId);
}





















//
//    public boolean deleteById(int id) {
//        return friendships.removeIf(f -> f.getId() == id);
//    }
//}