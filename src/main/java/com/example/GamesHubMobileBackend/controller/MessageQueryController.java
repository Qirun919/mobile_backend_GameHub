package com.example.GamesHubMobileBackend.controller;

import com.example.GamesHubMobileBackend.websocket.Message;
import com.example.GamesHubMobileBackend.services.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageQueryController {

    private final MessageService messageService;

    public MessageQueryController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/messages/private/{userId1}/{userId2}")
    public ResponseEntity<Object> getPrivateMessages(@PathVariable String userId1, @PathVariable String userId2) {
        List<Message> messages = messageService.getPrivateMessages(userId1, userId2);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/server/{serverId}")
    public ResponseEntity<Object> getServerMessages(@PathVariable String serverId) {
        List<Message> messages = messageService.getServerMessages(serverId);
        return ResponseEntity.ok(messages);
    }
}