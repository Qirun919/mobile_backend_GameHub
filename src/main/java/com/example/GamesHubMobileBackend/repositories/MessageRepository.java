package com.example.GamesHubMobileBackend.repositories;

import com.example.GamesHubMobileBackend.websocket.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
            String senderId1, String receiverId1, String senderId2, String receiverId2);

    List<Message> findByServerId(String serverId);
}