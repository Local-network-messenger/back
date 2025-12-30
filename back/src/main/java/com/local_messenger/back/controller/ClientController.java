package com.local_messenger.back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.local_messenger.back.client.FrontClient;
import com.local_messenger.back.model.api.rest.LatestHistoryRequest;
import com.local_messenger.back.model.api.rest.LatestHistoryResponse;
import com.local_messenger.back.model.api.rest.NickRequest;
import com.local_messenger.back.model.api.rest.NickResponse;
import com.local_messenger.back.model.api.rest.RegisterRequest;
import com.local_messenger.back.model.api.rest.RegisterResponse;
import com.local_messenger.back.model.api.rest.StatusResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "Client Controller", description = "Client related endpoints")
@Slf4j
public class ClientController {

    private final FrontClient frontClient;

    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getStatus() {
        StatusResponse status = frontClient.getStatus();
        log.info("🟣✅ [ClientController] GET /status → {}", status);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> postRegister(final @RequestBody @Valid RegisterRequest registerRequest) {
        RegisterResponse response = frontClient.register(registerRequest);
        log.info("🟢✅ [ClientController] POST /register: req={} → resp={}", registerRequest, response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/nick")
    public ResponseEntity<NickResponse> putNick(final @RequestBody @Valid NickRequest nickRequest) {
        NickResponse response = frontClient.updateNick(nickRequest);
        log.info("🟡✅ [ClientController] PUT /nick: req={} → resp={}", nickRequest, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latestHistory")
    public ResponseEntity<LatestHistoryResponse> getLatestHistory(
            final @RequestBody @Valid LatestHistoryRequest latestHistoryRequest) {
        LatestHistoryResponse response = frontClient.getLatestHistory(latestHistoryRequest);
        log.info("🔵✅ [ClientController] GET /latestHistory: req={} → resp={}", latestHistoryRequest, response);
        return ResponseEntity.ok(response);
    }
}
