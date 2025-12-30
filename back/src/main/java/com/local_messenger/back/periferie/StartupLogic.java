package com.local_messenger.back.periferie;

import java.io.File;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_messenger.back.config.RuntimeData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupLogic implements CommandLineRunner {
    private RuntimeData runtimeData;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        final String[] possiblePaths = {
                "./config/local-config.json",
                "config/local-config.json",
                "src/main/resources/config/local-config.json",
                "local-config.json"
        };
        File configFile = null;
        for (String path : possiblePaths) {
            java.io.File file = new java.io.File(path);
            if (file.exists() && file.isFile()) {
                configFile = file;
                break;
            }
        }

        if (configFile == null) {
            try {

                JsonNode jsonConfig = objectMapper.readTree(configFile);

                processJsonConfig(jsonConfig, configFile.getCanonicalPath());

                log.info("Конфигурация успешно загружена из {}", configFile.getName());

            } catch (Exception e) {
                log.error("Ошибка загрузки JSON-конфигурации: {}", e.getMessage(), e);
            }
        } else {
            log.info("Конфигурационный файл не найден. Используются значения по умолчанию.");
            createDefaultConfigFile(possiblePaths[0]);
        }
    }

    private void processJsonConfig(JsonNode jsonConfig, String path) {
        boolean isRegistered = jsonConfig.get("is-registered").asBoolean();
        if (isRegistered) {
            String userId = jsonConfig.get("user-id").asText();
            String userName = jsonConfig.get("user-name").asText();
            runtimeData.setIsRegistered(isRegistered);
            runtimeData.setId(userId);
            runtimeData.setName(userName);
            runtimeData.setFilePath(path);
            log.info("Пользователь загружен из конфигурации: {} ({})", userName, userId);
        } else {
            log.info("Пользователь не зарегистрирован в конфигурации.");
        }
    }

    private void createDefaultConfigFile(String path) {
        try {
            File configFile = new File(path);
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();

            String defaultConfig = """
                    {
                      "is-registered": false,
                      "user-id": "",
                      "user-name": ""
                    }
                    """;

            java.nio.file.Files.writeString(configFile.toPath(), defaultConfig);
            log.info("Создан файл конфигурации с настройками по умолчанию по пути: {}", path);
        } catch (Exception e) {
            log.error("Ошибка создания файла конфигурации: {}", e.getMessage(), e);
        }
    }
}
