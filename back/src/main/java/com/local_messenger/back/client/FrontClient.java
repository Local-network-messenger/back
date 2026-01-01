package com.local_messenger.back.client;

import java.security.Principal;
import java.util.Map;

import com.local_messenger.back.controller.WebSocketOutputController;
import com.local_messenger.back.model.api.ws.FoundUserResponse;
import com.local_messenger.back.model.api.ws.StartDiscoveryRequest;
import com.local_messenger.back.model.mdns.MdnsDiscoveredServiceInfo;
import com.local_messenger.back.service.MdnsRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FrontClient {
    private final MdnsRegistrationService mdnsRegistrationService;
    private final WebSocketOutputController webSocketOutputController;

    public void startDiscovery(final StartDiscoveryRequest startDiscoveryRequest, final Principal payload) {
        log.info("🔍 Начало поиска сервисов для пользователя: {}", payload.getName());
        final Map<String, MdnsDiscoveredServiceInfo> curServices = mdnsRegistrationService.getRegistrations();
        log.debug("✅ Найдено {} сервисов во время поиска", curServices.size());
        curServices.values().forEach(service -> {
            final FoundUserResponse response =
                (FoundUserResponse.builder().ip(service.getIp()).nick(service.getNick())
                    .port(service.getPort())).peerId(service.getPeerId()).build();
            log.debug("📤 Отправка ответа о найденном пользователе: ник={}, ip={}, port={}", service.getNick(),
                service.getIp(), service.getPort());
            webSocketOutputController.sendFoundUser(response);
        });
        log.info("✨ Поиск завершён для пользователя: {}", payload.getName());
    }


}
