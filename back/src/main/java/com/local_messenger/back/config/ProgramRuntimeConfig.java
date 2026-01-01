package com.local_messenger.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.local_messenger.back.model.periferie.RuntimeData;

@Configuration
public class ProgramRuntimeConfig {
    @Bean
    public RuntimeData runtimeData() {
        return new RuntimeData();
    }
}
