package com.local_messenger.back.client;

import org.springframework.stereotype.Service;

import com.local_messenger.back.config.RuntimeData;
import com.local_messenger.back.model.api.rest.LatestHistoryRequest;
import com.local_messenger.back.model.api.rest.LatestHistoryResponse;
import com.local_messenger.back.model.api.rest.NickRequest;
import com.local_messenger.back.model.api.rest.NickResponse;
import com.local_messenger.back.model.api.rest.RegisterRequest;
import com.local_messenger.back.model.api.rest.RegisterResponse;
import com.local_messenger.back.model.api.rest.StatusResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FrontClient {

    private final RuntimeData runtimeData;

    public StatusResponse getStatus() {
        StatusResponse status = StatusResponse.builder()
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
        RegisterResponse response = RegisterResponse.builder()
                .id(runtimeData.getId())
                .nick(registerRequest.getNick())
                .build();
        log.info("🟣 [FrontClient] register req={} → resp={}", registerRequest, response);
        return response;
    }

    public NickResponse updateNick(final NickRequest nickRequest) {
        runtimeData.setName(nickRequest.getNewNick());
        NickResponse response = NickResponse.builder()
                .nick(runtimeData.getName())
                .id(runtimeData.getId())
                .build();
        log.info("🟡 [FrontClient] updateNick req={} → resp={}", nickRequest, response);
        return response;
    }

    public LatestHistoryResponse getLatestHistory(final LatestHistoryRequest latestHistoryRequest) {
        LatestHistoryResponse response = new LatestHistoryResponse();
        log.info("🔵 [FrontClient] getLatestHistory req={} → resp={}", latestHistoryRequest, response);
        return response;
    }
}
