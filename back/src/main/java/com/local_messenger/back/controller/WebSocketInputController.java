package com.local_messenger.back.controller;

import java.security.Principal;

import com.local_messenger.back.client.FrontClient;
import com.local_messenger.back.model.api.ws.StartDiscoveryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketInputController {
    private final FrontClient frontClient;

    @MessageMapping("/discovery/start")
    public void startDiscovery(@Payload final StartDiscoveryRequest request, final Principal principal) {
        log.info("📨 Получен запрос на начало поиска от пользователя: {}", principal.getName());
        frontClient.startDiscovery(request, principal);
    }
}
