package com.local_messenger.back.periferie;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
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
    private final RuntimeData runtimeData;
    private final ObjectMapper objectMapper;
    @Value("${back.logPath}")
    private String defaultFilePath;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 [StartupLogic] Запуск инициализации конфигурации...");

        File configFile = null;

        java.io.File file = new java.io.File(defaultFilePath);
        log.debug("🔍 [StartupLogic] Проверка наличия файла конфигурации: {}", defaultFilePath);
        if (file.exists() && file.isFile()) {
            configFile = file;
            log.info("📄 [StartupLogic] Найден файл конфигурации: {}", defaultFilePath);
        }

        if (configFile != null) {
            try {
                log.info("📥 [StartupLogic] Загрузка конфигурации из файла: {}", configFile.getAbsolutePath());
                JsonNode jsonConfig = objectMapper.readTree(configFile);
                processJsonConfig(jsonConfig, configFile.getCanonicalPath());
                log.info("✅ [StartupLogic] Конфигурация успешно загружена из {}", configFile.getName());
            } catch (Exception e) {
                log.error("❌ [StartupLogic] Ошибка загрузки JSON-конфигурации: {}", e.getMessage(), e);
            }
        } else {
            log.warn("⚠️ [StartupLogic] Конфигурационный файл не найден. Используются значения по умолчанию.");
            createDefaultConfigFile(defaultFilePath);
        }

        runtimeData.setFilePath(defaultFilePath);
        log.info("🚀 [StartupLogic] Инициализация конфигурации завершена {}", runtimeData);
    }

    private void processJsonConfig(JsonNode jsonConfig, String path) {
        boolean isRegistered = jsonConfig.get("is-registered").asBoolean();
        log.debug("🔎 [StartupLogic] processJsonConfig: is-registered = {}", isRegistered);
        if (isRegistered) {
            String userId = jsonConfig.get("user-id").asText();
            String userName = jsonConfig.get("user-name").asText();
            runtimeData.setIsRegistered(isRegistered);
            runtimeData.setId(userId);
            runtimeData.setName(userName);
            runtimeData.setFilePath(path);
            log.info("🙋‍♂️ [StartupLogic] Пользователь загружен из конфигурации: {} ({})", userName, userId);
        } else {
            log.info("🙅‍♂️ [StartupLogic] Пользователь не зарегистрирован в конфигурации.");
        }
    }

    private void createDefaultConfigFile(String path) {
        try {
            File configFile = new File(path);
            if (configFile.getParentFile() != null) {
                configFile.getParentFile().mkdirs();
            }
            boolean created = configFile.createNewFile();
            log.debug("🆕 [StartupLogic] createDefaultConfigFile: файл создан? {}", created);

            String defaultConfig = """
                    {
                      "is-registered": false,
                      "user-id": "",
                      "user-name": ""
                    }
                    """;
            java.nio.file.Files.writeString(configFile.toPath(), defaultConfig);
            log.info("📝 [StartupLogic] Создан файл конфигурации с настройками по умолчанию по пути: {}", path);
        } catch (Exception e) {
            log.error("❌ [StartupLogic] Ошибка создания файла конфигурации: {}", e.getMessage(), e);
        }
    }
}
