package com.example.GamesHubMobileBackend.services;

import com.example.GamesHubMobileBackend.models.Friendship;
import com.example.GamesHubMobileBackend.models.User;
import com.example.GamesHubMobileBackend.repositories.FriendshipRepository;
import com.example.GamesHubMobileBackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendShipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendShipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public List<Friendship> getFriendships() {
        return friendshipRepository.findAll();
    }

    public Friendship getFriendshipById(String id) {
        return friendshipRepository.findById(id).orElse(null);
    }

    public List<Friendship> getFriendshipsByUserId(String userId) {
        return friendshipRepository.findByUserIdOrFriendId(userId, userId);
    }

    public boolean usersExist(String userId, String friendId) {
        User user = userRepository.findById(userId).orElse(null);
        User friend = userRepository.findById(friendId).orElse(null);
        return user != null && friend != null;
    }

    public Friendship getExisting(String userId, String friendId) {
        List<Friendship> possible = friendshipRepository.findByUserIdOrFriendId(userId, userId);
        for (Friendship f : possible) {
            boolean samePair = (f.getUserId().equals(userId) && f.getFriendId().equals(friendId))
                    || (f.getUserId().equals(friendId) && f.getFriendId().equals(userId));
            if (samePair) {
                return f;
            }
        }
        return null;
    }

    public Friendship saveFriendship(String userId, String friendId) {
        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus("pending");
        return friendshipRepository.save(friendship);
    }

    public Friendship updateFriendshipStatus(String id, String status) {
        Friendship existing = friendshipRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setStatus(status);
        return friendshipRepository.save(existing);
    }

    public boolean deleteFriendship(String id) {
        friendshipRepository.deleteById(id);
        return true;
    }

}

