package com.example.GamesHubMobileBackend.services;

import com.example.GamesHubMobileBackend.models.CommunityServer;
import com.example.GamesHubMobileBackend.models.User;
import com.example.GamesHubMobileBackend.repositories.CommunityServerRepository;
import com.example.GamesHubMobileBackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommunityServerService {

    private final CommunityServerRepository communityServerRepository;
    private final UserRepository userRepository;

    public CommunityServerService(CommunityServerRepository communityServerRepository, UserRepository userRepository) {
        this.communityServerRepository = communityServerRepository;
        this.userRepository = userRepository;
    }

    public List<CommunityServer> getServers() {
        List<CommunityServer> servers = communityServerRepository.findAll();
        for (CommunityServer server : servers) {
            fillUsers(server);
        }
        return servers;
    }

    public CommunityServer getServerById(String id) {
        CommunityServer server = communityServerRepository.findById(id).orElse(null);
        if (server != null) {
            fillUsers(server);
        }
        return server;
    }

    public boolean userExists(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null;
    }

    public CommunityServer saveServer(CommunityServer server) {
        if (server.getUserIds() == null) {
            server.setUserIds(new java.util.ArrayList<>());
        }
        if (server.getAdminIds() != null) {
            for (String adminId : server.getAdminIds()) {
                if (!server.getUserIds().contains(adminId)) {
                    server.getUserIds().add(adminId);
                }
            }
        }
        return communityServerRepository.save(server);
    }

    public CommunityServer updateServer(String id, CommunityServer server) {
        CommunityServer existing = communityServerRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setName(server.getName());
        existing.setDescription(server.getDescription());
        return communityServerRepository.save(existing);
    }

    public CommunityServer addMember(String serverId, String userId) {
        CommunityServer server = communityServerRepository.findById(serverId).orElse(null);

        if (server == null) {
            return null;
        }

        if (!userExists(userId)) {
            return null;
        }

        if (server.getUserIds() == null) {
            server.setUserIds(new java.util.ArrayList<>());
        }

        if (!server.getUserIds().contains(userId)) {
            server.getUserIds().add(userId);
        }

        // todo: need checking on user exist and return null if doesnt have. (DONE)
        return communityServerRepository.save(server);
    }

    public CommunityServer removeMember(String serverId, String userId, String requesterId) {
        CommunityServer server = communityServerRepository.findById(serverId).orElse(null);
        if (server == null) {
            return null;
        }

        // self leaving
        if (requesterId == userId) {
            if (server.getUserIds() != null) {
                server.getUserIds().remove(Integer.valueOf(userId));
            }
            return communityServerRepository.save(server);

        // kick member if User == Admin
        } else if (server.getAdminIds() != null && server.getAdminIds().contains(requesterId)) {
            if (server.getUserIds() != null) {
                server.getUserIds().remove(Integer.valueOf(userId));
            }
            return communityServerRepository.save(server);

        } else {
            return null;
        }
    }

    public boolean deleteServer(String id) {
        communityServerRepository.deleteById(id);
        return true;
    }

    private void fillUsers(CommunityServer server) {
        List<User> admins = new ArrayList<>();
        if (server.getAdminIds() != null) {
            for (String adminId : server.getAdminIds()) {
                User u = userRepository.findById(adminId).orElse(null);
                if (u != null) {
                    admins.add(u);
                }
            }
        }
        server.setAdmins(admins);

        List<User> users = new ArrayList<>();
        if (server.getUserIds() != null) {
            for (String userId : server.getUserIds()) {
                User u = userRepository.findById(userId).orElse(null);
                if (u != null) {
                    users.add(u);
                }
            }
        }
        server.setUsers(users);
    }
}
