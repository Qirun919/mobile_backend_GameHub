package com.example.GamesHubMobileBackend.controller;

import com.example.GamesHubMobileBackend.RequestModels.AddFriendRequestModel;
import com.example.GamesHubMobileBackend.RequestModels.UpdateFriendRequestModel;
import com.example.GamesHubMobileBackend.models.Friendship;
import com.example.GamesHubMobileBackend.services.FriendShipService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FriendshipController {

    private FriendShipService friendShipService;
    private final SimpMessagingTemplate messagingTemplate;

    public FriendshipController(FriendShipService friendShipService, SimpMessagingTemplate messagingTemplate) {
        this.friendShipService = friendShipService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping (
            path ="/friendships"
    )
    public ResponseEntity getFriendShips() {
        var currentFriendships = friendShipService.getFriendships();
        if (CollectionUtils.isEmpty(currentFriendships)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentFriendships);
    }

    @GetMapping(path = "/friendships/{id}")
    public ResponseEntity<Object> getFriendshipById(@PathVariable String id) {
        Friendship friendship = friendShipService.getFriendshipById(id);

        if (friendship == null) {
            return ResponseEntity.badRequest().body("Friend Not Found");
        }
        return ResponseEntity.ok(friendship);
    }

    @GetMapping(path = "/friendships/user/{userId}")
    public ResponseEntity<Object> getFriendshipByUserId(@PathVariable String userId) {
        List<Friendship> friendship = friendShipService.getFriendshipsByUserId(userId);

        if (friendship.isEmpty()) {
            return ResponseEntity.badRequest().body("Friend Not Found");
        }
        return ResponseEntity.ok(friendship);
    }

    @PostMapping(path = "/friendships")
    // todo: Need a request model, put only neccesary variable for front end to see. Front end might confuse. (DONE)
    public ResponseEntity<Object> addFriendship(@RequestBody AddFriendRequestModel request) {

        if (request.getUserId() == request.getFriendId()) {
            return ResponseEntity.badRequest().body("Cannot add yourself as a friend");
        }

        if (!friendShipService.usersExist(request.getUserId(), request.getFriendId())) {
            return ResponseEntity.badRequest().body("User or friend does not exist");
        }

        Friendship existing = friendShipService.getExisting(request.getUserId(), request.getFriendId());
        if (existing != null) {
            return ResponseEntity.badRequest().body("Friend already exists");
        }

        Friendship saved = friendShipService.saveFriendship(request.getUserId(), request.getFriendId());

        messagingTemplate.convertAndSendToUser(request.getFriendId(), "/queue/friend-request", saved);

        return ResponseEntity.ok(saved);
    }

    @PutMapping(path = "/friendships/{id}")
    // todo: need to have request model too. (DONE)
    public ResponseEntity<Object> updateFriendship(@PathVariable String id, @RequestBody UpdateFriendRequestModel request) {

        if (!"accepted".equals(request.getStatus()) && !"pending".equals(request.getStatus())) {
            return ResponseEntity.badRequest().body("Status must be 'pending' or 'accepted'");
        }

        Friendship updated = friendShipService.updateFriendshipStatus(id, request.getStatus());
        if (updated == null) {
            return ResponseEntity.badRequest().body("Friend Not Found");
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "/friendships/{id}")
    public ResponseEntity<Object> deleteFriendship(@PathVariable String id) {
        friendShipService.deleteFriendship(id);
        return ResponseEntity.ok("Friend Have Been Deleted");
    }


}

