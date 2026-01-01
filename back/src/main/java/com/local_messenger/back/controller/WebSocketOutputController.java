package com.local_messenger.back.controller;

import com.local_messenger.back.model.api.ws.FoundUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;

@Slf4j
@Service
public class WebSocketOutputController {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketOutputController(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendFoundUser(final FoundUserResponse response) {
        log.info("✅🟢 [WebSocketOutputController] Отправка FoundUserResponse через WebSocket: {}", response);
        messagingTemplate.convertAndSend("/user/discovery-results", response);
    }

    public void sendError(final ErrorResponse response) {
        log.warn("❌🟠 [WebSocketOutputController] Отправка ошибки через WebSocket: {}", response);
        messagingTemplate.convertAndSend("/user/errors", response);
    }
}
