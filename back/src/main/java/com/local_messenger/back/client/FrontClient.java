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
        final Map<String, MdnsDiscoveredServiceInfo> curServices = mdnsRegistrationService.getRegistrations();
        curServices.values().forEach(service -> {
            final FoundUserResponse response =
                (FoundUserResponse.builder().ip(service.getIp()).nick(service.getNick())
                    .port(service.getPort())).peerId(service.getPeerId()).build();
            webSocketOutputController.sendFoundUser(response);
        });
    }


}
