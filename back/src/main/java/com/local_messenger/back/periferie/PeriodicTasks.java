package com.local_messenger.back.periferie;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_messenger.back.model.periferie.FileData;
import com.local_messenger.back.model.periferie.RuntimeData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
                log.debug("⏭️ [PeriodicTasks] Runtime is not registered, skipping file write.");
                return;
            }
            final String filePath = runtimeData.getFilePath();
            if (filePath == null || filePath.isBlank()) {
                log.warn("❗ [PeriodicTasks] filePath is null or blank in runtimeData! Не могу записать файл. {}",
                    runtimeData);
                return;
            }
            final File curFile = new File(filePath);
            if (curFile.exists() && curFile.isFile()) {
                final FileData fileData = FileData.builder()
                    .isRegistered(runtimeData.getIsRegistered())
                    .id(runtimeData.getId())
                    .name(runtimeData.getName())
                    .build();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(curFile, fileData);
                log.info("💾✅ [PeriodicTasks] Wrote runtime data to file: {} | data: {}", curFile.getAbsolutePath(),
                    fileData);
            } else {
                log.warn("❗ [PeriodicTasks] File does not exist or is not a file: {}", curFile.getAbsolutePath());
            }
        } catch (final Exception e) {
            log.error("❌ [PeriodicTasks] Error in pullDataFromRuntime: {}", e.getMessage(), e);
        }
    }
}
