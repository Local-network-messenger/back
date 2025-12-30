package com.local_messenger.back.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

import com.local_messenger.back.model.api.ws.FoundUserResponse;

@Service
public class WebSocketOutputController {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketOutputController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendFoundUser(final FoundUserResponse response) {
        messagingTemplate.convertAndSend("/user/discovery-results", response);
    }

    public void sendError(final ErrorResponse response) {
        messagingTemplate.convertAndSend("/user/errors", response);
    }
}
