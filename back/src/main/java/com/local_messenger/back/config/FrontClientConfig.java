package com.local_messenger.back.config;

import org.springframework.context.annotation.Configuration;

import com.local_messenger.back.client.FrontClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FrontClientConfig {

    private final RuntimeData runtimeData;

    public FrontClient frontClient() {
        return new FrontClient(runtimeData);
    }
}
