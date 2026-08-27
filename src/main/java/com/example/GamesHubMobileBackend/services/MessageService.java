package com.example.GamesHubMobileBackend.services;

import com.example.GamesHubMobileBackend.websocket.Message;
import com.example.GamesHubMobileBackend.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getPrivateMessages(String userId1, String userId2) {
        return messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
                userId1, userId2, userId2, userId1);
    }

    public List<Message> getServerMessages(String serverId) {
        return messageRepository.findByServerId(serverId);
    }
}