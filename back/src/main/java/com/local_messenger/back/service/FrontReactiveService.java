package com.local_messenger.back.service;

import com.local_messenger.back.model.api.rest.LatestHistoryRequest;
import com.local_messenger.back.model.api.rest.LatestHistoryResponse;
import com.local_messenger.back.model.api.rest.NickRequest;
import com.local_messenger.back.model.api.rest.NickResponse;
import com.local_messenger.back.model.api.rest.RegisterRequest;
import com.local_messenger.back.model.api.rest.RegisterResponse;
import com.local_messenger.back.model.api.rest.StatusResponse;
import com.local_messenger.back.model.periferie.RuntimeData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FrontReactiveService {
    private final RuntimeData runtimeData;

    public StatusResponse getStatus() {
        final StatusResponse status = StatusResponse.builder()
            .registered(runtimeData.getIsRegistered())
            .nick(runtimeData.getName())
            .id(runtimeData.getId())
            .build();
        log.info("🟢 [FrontClient] getStatus → {}", status);
        return status;
    }

    public RegisterResponse register(final RegisterRequest registerRequest) {
        runtimeData.setIsRegistered(true);
        runtimeData.setName(registerRequest.getNick());
        runtimeData.setId(java.util.UUID.randomUUID().toString());
        final RegisterResponse response = RegisterResponse.builder()
            .id(runtimeData.getId())
            .nick(registerRequest.getNick())
            .build();
        log.info("🟣 [FrontClient] register req={} → resp={}", registerRequest, response);
        return response;
    }

    public NickResponse updateNick(final NickRequest nickRequest) {
        runtimeData.setName(nickRequest.getNewNick());
        final NickResponse response = NickResponse.builder()
            .nick(runtimeData.getName())
            .id(runtimeData.getId())
            .build();
        log.info("🟡 [FrontClient] updateNick req={} → resp={}", nickRequest, response);
        return response;
    }

    public LatestHistoryResponse getLatestHistory(final LatestHistoryRequest latestHistoryRequest) {
        final LatestHistoryResponse response = new LatestHistoryResponse();
        log.info("🔵 [FrontClient] getLatestHistory req={} → resp={}", latestHistoryRequest, response);
        return response;
    }
}
