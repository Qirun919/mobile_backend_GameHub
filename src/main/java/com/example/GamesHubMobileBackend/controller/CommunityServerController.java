package com.example.GamesHubMobileBackend.controller;

import com.example.GamesHubMobileBackend.models.CommunityServer;
import com.example.GamesHubMobileBackend.services.CommunityServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommunityServerController {
    private CommunityServerService communityServerService;

    public CommunityServerController(CommunityServerService communityServerService) {
        this.communityServerService = communityServerService;
    }

    @GetMapping("/servers")
    // todo: validation on admin id and user id
    public ResponseEntity getServers() {
        var currentServers = communityServerService.getServers();
        if (CollectionUtils.isEmpty(currentServers)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentServers);
    }

    @GetMapping("/servers/{id}")
    public ResponseEntity<Object> getServerById(@PathVariable String id) {
        CommunityServer server = communityServerService.getServerById(id);
        if (server == null) {
            return ResponseEntity.badRequest().body("Server Not Found.");
        }
        return ResponseEntity.ok(server);
    }

    @PostMapping("/servers")
    public ResponseEntity<Object> addServer(@RequestBody CommunityServer server) {
        if (server.getName() == null || server.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Server name is required.");
        }

        if (server.getAdminIds() == null || server.getAdminIds().isEmpty()) {
            return ResponseEntity.badRequest().body("At least one admin is required.");
        }

        for (String adminId : server.getAdminIds()) {
            if (!communityServerService.userExists(adminId)) {
                return ResponseEntity.badRequest().body("Admin user " + adminId + " does not exist.");
            }
        }

        return ResponseEntity.ok(communityServerService.saveServer(server));
    }

    @PostMapping("/servers/{serverId}/join/{userId}")
    // todo: validation on user id
    public ResponseEntity<Object> joinServer(@PathVariable String serverId, @PathVariable String userId) {
        CommunityServer updated = communityServerService.addMember(serverId, userId);
        if (updated == null) {
            return ResponseEntity.badRequest().body("Server Not Found.");
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/servers/{serverId}/leave/{userId}")
    public ResponseEntity<Object> leaveServer(
            @PathVariable String serverId,
            @PathVariable String userId,
            @RequestParam String requesterId) {

        CommunityServer updated = communityServerService.removeMember(serverId, userId, requesterId);
        if (updated == null) {
            return ResponseEntity.badRequest().body("you don't have permission to remove this member.");
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/servers/{id}")
    public ResponseEntity<Object> deleteServer(@PathVariable String id) {
        communityServerService.deleteServer(id);
        return ResponseEntity.ok("Server Have Been Deleted");
    }
}
