package com.example.GamesHubMobileBackend.controller;

import com.example.GamesHubMobileBackend.websocket.Message;
import com.example.GamesHubMobileBackend.repositories.MessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class MessageController {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.private")
    public void sendPrivateMessage(Message message) {
        message.setTimestamp(new Date());
        messageRepository.save(message);

        messagingTemplate.convertAndSendToUser(message.getReceiverId(), "/queue/messages", message);
    }

    @MessageMapping("/chat.group")
    public void sendGroupMessage(Message message) {
        message.setTimestamp(new Date());
        messageRepository.save(message);

        messagingTemplate.convertAndSend("/topic/server/" + message.getServerId(), message);
    }
}