package com.local_messenger.back.periferie;

import java.io.File;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_messenger.back.config.RuntimeData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class PeriodicTasks {
    private final ObjectMapper objectMapper;
    private final RuntimeData runtimeData;

    @Scheduled(fixedRate = 10000, initialDelay = 5000)
    public void pullDataFromRuntime() {
        try {
            if (!runtimeData.getIsRegistered()) {
                return;
            }
            File curFile = new File(runtimeData.getFilePath());
            if (curFile.exists() && curFile.isFile()) {
                FileData fileData = FileData.builder()
                        .isRegistered(runtimeData.getIsRegistered())
                        .id(runtimeData.getId())
                        .name(runtimeData.getName())
                        .build();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(curFile, fileData);
            }
        } catch (Exception e) {
            log.info("[PeriodicTasks] Error in pullDataFromRuntime: " + e.getMessage());
            log.info("[PeriodicTasks] Stack trace: ", e);
        }
    }
}
