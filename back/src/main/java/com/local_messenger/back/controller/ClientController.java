package com.local_messenger.back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "Client Controller", description = "Client related endpoints")
class ClientController {
    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getStatus() {
        return ResponseEntity.ok(new StatusResponse());
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> postRegister(final @RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.ok(new RegisterResponse());
    }

    @PutMapping("/nick")
    public ResponseEntity<NickResponse> putNick(final @RequestBody @Valid NickRequest nickRequest) {
        return ResponseEntity.ok(new NickResponse());
    }

    @GetMapping("/latestHistory")
    public LatestHistoryResponse getLatestGistory(final @RequestBody @Valid LatestHistoryRequest latestHistoryRequest) {
        return new LatestHistoryResponse();
    }
}
