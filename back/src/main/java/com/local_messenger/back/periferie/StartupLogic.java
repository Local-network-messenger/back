package com.local_messenger.back.periferie;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_messenger.back.model.periferie.RuntimeData;

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
    public void run(final String... args) throws Exception {
        log.info("🚀 [StartupLogic] Запуск инициализации конфигурации...");

        File configFile = null;

        final File file = new java.io.File(defaultFilePath);
        log.debug("🔍 [StartupLogic] Проверка наличия файла конфигурации: {}", defaultFilePath);
        if (file.exists() && file.isFile()) {
            configFile = file;
            log.info("📄 [StartupLogic] Найден файл конфигурации: {}", defaultFilePath);
        }

        if (configFile != null) {
            try {
                log.info("📥 [StartupLogic] Загрузка конфигурации из файла: {}", configFile.getAbsolutePath());
                final JsonNode jsonConfig = objectMapper.readTree(configFile);
                processJsonConfig(jsonConfig, configFile.getCanonicalPath());
                log.info("✅ [StartupLogic] Конфигурация успешно загружена из {}", configFile.getName());
            } catch (final Exception e) {
                log.error("❌ [StartupLogic] Ошибка загрузки JSON-конфигурации: {}", e.getMessage(), e);
            }
        } else {
            log.warn("⚠️ [StartupLogic] Конфигурационный файл не найден. Используются значения по умолчанию.");
            createDefaultConfigFile(defaultFilePath);
        }

        runtimeData.setFilePath(defaultFilePath);
        log.info("🚀 [StartupLogic] Инициализация конфигурации завершена {}", runtimeData);
    }

    private void processJsonConfig(final JsonNode jsonConfig, final String path) {
        final JsonNode isRegisteredNode = jsonConfig.get("registered");
        if (isRegisteredNode == null) {
            log.warn("⚠️ [StartupLogic] Поле 'isRegistered' отсутствует в конфигурации");
            return;
        }
        final boolean isRegistered = isRegisteredNode.asBoolean();
        log.debug("🔎 [StartupLogic] processJsonConfig: isRegistered = {}", isRegistered);
        if (isRegistered) {
            final JsonNode idNode = jsonConfig.get("id");
            final JsonNode nameNode = jsonConfig.get("name");
            if (idNode != null && nameNode != null) {
                final String userId = idNode.asText();
                final String userName = nameNode.asText();
                runtimeData.setIsRegistered(isRegistered);
                runtimeData.setId(userId);
                runtimeData.setName(userName);
                runtimeData.setFilePath(path);
                log.info("🙋‍♂️ [StartupLogic] Пользователь загружен из конфигурации: {} ({})", userName, userId);
            } else {
                log.warn("⚠️ [StartupLogic] Отсутствуют обязательные поля 'id' или 'name' в конфигурации");
            }
        } else {
            log.info("🙅‍♂️ [StartupLogic] Пользователь не зарегистрирован в конфигурации.");
        }
    }

    private void createDefaultConfigFile(final String path) {
        try {
            final File configFile = new File(path);
            if (configFile.getParentFile() != null) {
                configFile.getParentFile().mkdirs();
            }
            final boolean created = configFile.createNewFile();
            log.debug("🆕 [StartupLogic] createDefaultConfigFile: файл создан? {}", created);

            final String defaultConfig = """
                    {
                      "isRegistered": false,
                      "id": "",
                      "name": ""
                    }
                    """;
            java.nio.file.Files.writeString(configFile.toPath(), defaultConfig);
            log.info("📝 [StartupLogic] Создан файл конфигурации с настройками по умолчанию по пути: {}", path);
        } catch (final Exception e) {
            log.error("❌ [StartupLogic] Ошибка создания файла конфигурации: {}", e.getMessage(), e);
        }
    }
}
