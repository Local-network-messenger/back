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

@Service
@RequiredArgsConstructor
public class FrontClient {

    private final RuntimeData runtimeData;

    public StatusResponse getStatus() {
        return StatusResponse.builder()
                .registered(runtimeData.getIsRegistered())
                .nick(runtimeData.getName())
                .id(runtimeData.getId())
                .build();
    }

    public RegisterResponse register(final RegisterRequest registerRequest) {
        runtimeData.setIsRegistered(true);
        runtimeData.setName(registerRequest.getNick());
        runtimeData.setId(java.util.UUID.randomUUID().toString());
        return RegisterResponse.builder()
                .id(runtimeData.getId())
                .nick(registerRequest.getNick())
                .build();
    }

    public NickResponse updateNick(final NickRequest nickRequest) {
        runtimeData.setName(nickRequest.getNewNick());
        return NickResponse.builder()
                .nick(runtimeData.getName())
                .build();
    }

    public LatestHistoryResponse getLatestHistory(final LatestHistoryRequest latestHistoryRequest) {
        return new LatestHistoryResponse();
    }
}
