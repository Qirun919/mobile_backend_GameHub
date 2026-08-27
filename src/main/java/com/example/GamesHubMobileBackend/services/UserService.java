package com.example.GamesHubMobileBackend.services;

import com.example.GamesHubMobileBackend.models.Friendship;
import com.example.GamesHubMobileBackend.models.User;
import com.example.GamesHubMobileBackend.repositories.FriendshipRepository;
import com.example.GamesHubMobileBackend.repositories.UserRepository;
import com.example.GamesHubMobileBackend.util.JwtUtil;
import com.example.GamesHubMobileBackend.websocket.OnlineStatusMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SimpMessagingTemplate messagingTemplate;   // send to user
    private final FriendshipRepository friendshipRepository; // find user friend

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, SimpMessagingTemplate messagingTemplate, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.messagingTemplate = messagingTemplate;
        this.friendshipRepository = friendshipRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }


    public User updateUser(User user) {
        User existing = userRepository.findById(user.getId()).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
        existing.setAvatarUrl(user.getAvatarUrl());
        return existing;
    }

    public boolean deleteUser(String id) {
        userRepository.deleteById(id);
        return true;
    }

    public String login(String email, String password) {
        User u = userRepository.findByEmail(email);
        if (u != null && u.getPassword().equals(password)) {
            u.setOnline(true);
            userRepository.save(u);
            notifyFriendsOnlineStatus(u.getId(), true);
            return jwtUtil.generateToken(u.getId());
        }
        return null;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean logout(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        user.setOnline(false);
        userRepository.save(user);
        notifyFriendsOnlineStatus(userId, false);
        return true;
    }

    // check who is User Friend and send it one by one
    private void notifyFriendsOnlineStatus(String userId, boolean online) {
        // find all friendid and userid wathever who that guy role is
        List<Friendship> friendships = friendshipRepository.findByUserIdOrFriendId(userId, userId);

        for (Friendship f : friendships) {
            // check who is B
            // if I am userID than B is friendID
            String friendUserId = f.getUserId().equals(userId) ? f.getFriendId() : f.getUserId();

            // send to friend
            // "/queue/online-status" this is the server who in this that will get notification
            messagingTemplate.convertAndSendToUser(friendUserId, "/queue/online-status", new OnlineStatusMessage(userId, online));
        }
    }


    // AvatarURL
    public User updateAvatar(String id, String avatarUrl) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        user.setAvatarUrl(avatarUrl);
        return user;
    }
}




