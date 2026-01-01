package com.local_messenger.back.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_messenger.back.model.mdns.MdnsDiscoveredServiceInfo;
import com.local_messenger.back.model.periferie.RuntimeData;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MdnsRegistrationService implements DisposableBean {

    @Getter
    private final Map<String, MdnsDiscoveredServiceInfo> registrations = new ConcurrentHashMap<>();

    private final RuntimeData runtimeData;
    private final ObjectMapper objectMapper;

    @Value("${spring.jmDNS.service-port:8080}")
    private Integer servicePort;

    @Value("${spring.jmDNS.service-name:messenger-unknown}")
    private String serviceName;

    @Value("${spring.jmDNS.service-type:_messenger._tcp.local.}")
    private String serviceType;

    @Value("${spring.jmDNS.discovery-enabled:true}")
    private Boolean discoveryEnabled;

    @Value("${spring.jmDNS.update-interval-seconds:10}")
    private Integer updateIntervalSeconds;

    @Value("${spring.jmDNS.name:LocalMessenger}")
    private String jmDnsName;

    @Value("${spring.jmDNS.jsonKey:runtimeData}")
    private String jsonKey;

    @Value("${spring.jmDNS.version:2.0}")
    private String jmDnsVersion;

    @Value("${spring.jmDNS.app:LocalMessenger}")
    private String jmDnsApp;

    @Value("${spring.jmDNS.timeout-request:5000}")
    private Integer jmDnsTimeoutRequest;

    @Value("${spring.jmDNS.timeout-refresh:5000}")
    private Integer jmDnsTimeoutRefresh;

    @Value("${spring.jmDNS.cleanup-timeout-seconds:30}")
    private Integer cleanupTimeoutSeconds;

    private ServiceInfo serviceInfo;
    private ScheduledExecutorService scheduledExecutorService;
    private JmDNS jmdns;
    private Integer curPort;
    private String curIp;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        log.info("🚀 [MdnsRegistrationService] Начало инициализации mDNS сервиса...");
        log.debug("🔍 [MdnsRegistrationService] RuntimeData: {}, ObjectMapper: {}",
                runtimeData != null ? "✅ инжектирован" : "❌ NULL",
                objectMapper != null ? "✅ инжектирован" : "❌ NULL");

        if (runtimeData == null) {
            log.error("❌ [MdnsRegistrationService] RuntimeData не инжектирован! Проверьте конфигурацию Spring.");
            return;
        }

        try {
            curPort = servicePort;
            curIp = getLocalIpAdress();
            log.info("🌐 [MdnsRegistrationService] Определён локальный IP: {}, порт: {}", curIp, curPort);

            final InetAddress localHost = InetAddress.getLocalHost();
            jmdns = JmDNS.create(localHost, jmDnsName);
            log.info("✅ [MdnsRegistrationService] JmDNS создан: {} на {}", jmDnsName, localHost.getHostAddress());

            registerService();
            log.info("📡 [MdnsRegistrationService] Сервис зарегистрирован в mDNS");

            if (Boolean.TRUE.equals(discoveryEnabled)) {
                startServiceDiscovery();
                log.info("🔍 [MdnsRegistrationService] Запущено обнаружение сервисов");
            } else {
                log.info("🚫 [MdnsRegistrationService] Обнаружение сервисов отключено");
            }

            startPeriodicUpdate();
            log.info("🔄 [MdnsRegistrationService] Запущено периодическое обновление (интервал: {}с)",
                    updateIntervalSeconds);

            startCleanupTask();
            log.info("🧹 [MdnsRegistrationService] Запущена задача очистки (таймаут: {}с)", cleanupTimeoutSeconds);

            log.info("✅🎉 [MdnsRegistrationService] Инициализация mDNS сервиса завершена успешно");
        } catch (final UnknownHostException e) {
            log.error("❌ [MdnsRegistrationService] Не удалось определить локальный хост: {}", e.getMessage(), e);
        } catch (final Exception e) {
            log.error("❌ [MdnsRegistrationService] Ошибка инициализации mDNS: {}", e.getMessage(), e);
        }
    }

    private String getLocalIpAdress() throws SocketException {
        log.debug("🔍 [MdnsRegistrationService] Поиск локального IP адреса...");
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            final NetworkInterface iface = interfaces.nextElement();
            if (!iface.isUp() || iface.isLoopback() || iface.isVirtual()) {
                log.debug("⏭️ [MdnsRegistrationService] Пропуск интерфейса: {} (up={}, loopback={}, virtual={})",
                        iface.getName(), iface.isUp(), iface.isLoopback(), iface.isVirtual());
                continue;
            }
            final Enumeration<InetAddress> adresses = iface.getInetAddresses();
            while (adresses.hasMoreElements()) {
                final InetAddress addr = adresses.nextElement();
                if (!addr.isLoopbackAddress()) {
                    log.info("✅ [MdnsRegistrationService] Найден IP: {} на интерфейсе {}", addr.getHostAddress(),
                            iface.getName());
                    return addr.getHostAddress();
                }
            }
        }
        log.warn("⚠️ [MdnsRegistrationService] Не найден подходящий IP, используется 127.0.0.1");
        return "127.0.0.1";
    }

    private void registerService() throws IOException {
        log.debug("📝 [MdnsRegistrationService] Создание JSON для регистрации сервиса...");
        final String peerJson = createPeerJson();
        log.debug("📄 [MdnsRegistrationService] JSON данные: {}", peerJson);

        final String localServiceName = generateServiceName();
        log.info("🏷️ [MdnsRegistrationService] Генерация имени сервиса: {}", localServiceName);

        final Map<String, String> txtRecords = new java.util.HashMap<>();
        txtRecords.put(jsonKey, peerJson);

        serviceInfo = ServiceInfo.create(
                serviceType,
                localServiceName,
                servicePort,
                0, 0,
                txtRecords);

        jmdns.registerService(serviceInfo);
        log.info("✅ [MdnsRegistrationService] Сервис зарегистрирован: type={}, name={}, port={}",
                serviceType, serviceName, servicePort);
    }

    private String generateServiceName() {
        final String uniqueName = serviceName + "-" + UUID.randomUUID();
        log.debug("🆔 [MdnsRegistrationService] Сгенерировано уникальное имя: {}", uniqueName);
        return uniqueName;
    }

    private void startServiceDiscovery() {
        log.info("🔎 [MdnsRegistrationService] Добавление слушателя для типа сервиса: {}", serviceType);
        jmdns.addServiceListener(serviceType, new ServiceListener() {
            @Override
            public void serviceAdded(final ServiceEvent event) {
                log.info("➕ [MdnsRegistrationService] Обнаружен новый сервис: {} (type: {})",
                        event.getName(), event.getType());
                jmdns.requestServiceInfo(event.getType(), event.getName(), jmDnsTimeoutRequest);
                log.debug("📨 [MdnsRegistrationService] Запрошена информация о сервисе: {}", event.getName());
            }

            @Override
            public void serviceRemoved(final ServiceEvent event) {
                final String localServiceName = event.getName();
                log.info("➖ [MdnsRegistrationService] Сервис удалён: {}", localServiceName);
                final MdnsDiscoveredServiceInfo removed = registrations.remove(localServiceName);
                if (removed != null) {
                    log.debug("🗑️ [MdnsRegistrationService] Удалён из регистрации: {} (peerId: {})",
                            localServiceName, removed.getPeerId());
                }
            }

            @Override
            public void serviceResolved(final ServiceEvent event) {
                final ServiceInfo localServiceInfo = event.getInfo();
                final String localServiceName = event.getName();
                log.info("✅ [MdnsRegistrationService] Сервис разрешён: {}", localServiceName);
                try {

                    final String jsonData = localServiceInfo.getPropertyString(jsonKey);
                    if (jsonData != null && !jsonData.trim().isEmpty()) {
                        log.debug("📄 [MdnsRegistrationService] Получены TXT данные для ключа '{}': {}",
                                jsonKey, jsonData);
                        final MdnsDiscoveredServiceInfo foundInfo = parseFromJson(jsonData);
                        if (foundInfo != null) {

                            if (foundInfo.getIp() == null || foundInfo.getIp().isEmpty()) {
                                final String[] addresses = localServiceInfo.getHostAddresses();
                                if (addresses != null && addresses.length > 0) {
                                    foundInfo.setIp(addresses[0]);
                                }
                            }
                            if (foundInfo.getPort() == null || foundInfo.getPort() == 0) {
                                foundInfo.setPort(localServiceInfo.getPort());
                            }
                            foundInfo.setLastSeenTimestamp(System.currentTimeMillis());
                            registrations.put(localServiceName, foundInfo);
                            log.info(
                                    "💾 [MdnsRegistrationService] Сохранён сервис: {} → peerId={}, nick={}, ip={}, port={}",
                                    localServiceName, foundInfo.getPeerId(), foundInfo.getNick(),
                                    foundInfo.getIp(), foundInfo.getPort());
                            log.debug("📊 [MdnsRegistrationService] Всего зарегистрировано сервисов: {}",
                                    registrations.size());
                        } else {
                            log.warn("⚠️ [MdnsRegistrationService] Не удалось распарсить JSON для сервиса: {}",
                                    localServiceName);
                        }
                    } else {
                        log.warn("⚠️ [MdnsRegistrationService] Пустые TXT данные для ключа '{}' в сервисе: {}",
                                jsonKey, localServiceName);
                    }
                } catch (final Exception e) {
                    log.error("❌ [MdnsRegistrationService] Ошибка парсинга информации о сервисе {}: {}",
                            localServiceName, e.getMessage(), e);
                }
            }
        });
    }

    private void startCleanupTask() {
        log.info("🧹 [MdnsRegistrationService] Настройка задачи очистки старых сервисов (интервал: {}с)",
                cleanupTimeoutSeconds);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                log.debug("🔄 [MdnsRegistrationService] Запуск задачи очистки...");
                cleanupOldServices();
            } catch (final Exception e) {
                log.error("❌ [MdnsRegistrationService] Ошибка в задаче очистки: {}", e.getMessage(), e);
            }
        }, cleanupTimeoutSeconds, cleanupTimeoutSeconds, TimeUnit.SECONDS);
    }

    private void cleanupOldServices() {
        final long currentTime = System.currentTimeMillis();
        final long timeoutMillis = cleanupTimeoutSeconds * 1000L;
        final int sizeBefore = registrations.size();

        log.debug("🧹 [MdnsRegistrationService] Очистка старых сервисов: проверка {} записей", sizeBefore);

        registrations.entrySet().removeIf(entry -> {
            final MdnsDiscoveredServiceInfo info = entry.getValue();
            final boolean shouldRemove = info.getLastSeenTimestamp() != null &&
                    (currentTime - info.getLastSeenTimestamp()) > timeoutMillis;

            if (shouldRemove) {
                final long age = (currentTime - info.getLastSeenTimestamp()) / 1000;
                log.info("🗑️ [MdnsRegistrationService] Удаление устаревшего сервиса: {} (возраст: {}с, peerId: {})",
                        entry.getKey(), age, info.getPeerId());
            }
            return shouldRemove;
        });

        final int sizeAfter = registrations.size();
        final int removed = sizeBefore - sizeAfter;
        if (removed > 0) {
            log.info("✅ [MdnsRegistrationService] Очистка завершена: удалено {} сервисов, осталось {}",
                    removed, sizeAfter);
        } else {
            log.debug("✅ [MdnsRegistrationService] Очистка завершена: удалений не требуется (всего: {})", sizeAfter);
        }
    }

    private void startPeriodicUpdate() {
        log.info("🔄 [MdnsRegistrationService] Настройка периодического обновления (интервал: {}с)",
                updateIntervalSeconds);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                log.debug("🔄 [MdnsRegistrationService] Запуск периодического обновления...");
                updateServiceRegistration();
                refreshDiscoveredServices();
                log.debug("✅ [MdnsRegistrationService] Периодическое обновление завершено");
            } catch (final Exception e) {
                log.error("❌ [MdnsRegistrationService] Ошибка в периодическом обновлении: {}", e.getMessage(), e);
            }
        }, updateIntervalSeconds, updateIntervalSeconds, TimeUnit.SECONDS);
    }

    private void updateServiceRegistration() {
        if (serviceInfo == null) {
            log.warn("⚠️ [MdnsRegistrationService] serviceInfo null, пропуск обновления регистрации");
            return;
        }

        try {
            log.debug("📝 [MdnsRegistrationService] Обновление регистрации сервиса: {}", serviceInfo.getName());
            final String peerJson = createPeerJson();
            log.debug("📄 [MdnsRegistrationService] Новые JSON данные: {}", peerJson);

            final ServiceInfo newServiceInfo = ServiceInfo.create(
                    serviceType,
                    serviceInfo.getName(),
                    servicePort,
                    0, 0,
                    peerJson);

            log.debug("🔓 [MdnsRegistrationService] Отмена регистрации старого сервиса...");
            jmdns.unregisterService(serviceInfo);
            Thread.sleep(100);
            log.debug("🔒 [MdnsRegistrationService] Регистрация обновлённого сервиса...");
            jmdns.registerService(newServiceInfo);
            serviceInfo = newServiceInfo;
            log.info("✅ [MdnsRegistrationService] Регистрация сервиса обновлена: {}", serviceInfo.getName());
        } catch (final Exception e) {
            log.error("❌ [MdnsRegistrationService] Ошибка обновления регистрации сервиса: {}", e.getMessage(), e);
        }
    }

    private void refreshDiscoveredServices() {
        final int serviceCount = registrations.size();
        log.debug("🔄 [MdnsRegistrationService] Обновление информации о {} обнаруженных сервисах", serviceCount);

        registrations.keySet().forEach(localServiceName -> {
            try {
                log.debug("📨 [MdnsRegistrationService] Запрос обновления для сервиса: {}", localServiceName);
                jmdns.requestServiceInfo(serviceType, localServiceName, jmDnsTimeoutRefresh);
            } catch (final Exception e) {
                log.error("❌ [MdnsRegistrationService] Ошибка запроса информации о сервисе {}: {}",
                        localServiceName, e.getMessage(), e);
            }
        });

        if (serviceCount > 0) {
            log.debug("✅ [MdnsRegistrationService] Запросы обновления отправлены для {} сервисов", serviceCount);
        }
    }

    private MdnsDiscoveredServiceInfo parseFromJson(final String jsonData) throws JsonProcessingException {
        if (jsonData == null || jsonData.trim().isEmpty() || jsonData.equals("{}")) {
            log.debug("⚠️ [MdnsRegistrationService] Пустые или невалидные JSON данные, пропуск парсинга");
            return null;
        }
        log.debug("📖 [MdnsRegistrationService] Парсинг JSON: {}", jsonData);
        final MdnsDiscoveredServiceInfo result = objectMapper.readValue(jsonData, MdnsDiscoveredServiceInfo.class);
        log.debug("✅ [MdnsRegistrationService] JSON успешно распарсен: peerId={}, nick={}",
                result.getPeerId(), result.getNick());
        return result;
    }

    private String createPeerJson() throws JsonProcessingException {
        if (runtimeData == null) {
            log.warn("⚠️ [MdnsRegistrationService] runtimeData null, возвращаем пустой JSON");
            return "{}";
        }

        final MdnsDiscoveredServiceInfo curInfo = MdnsDiscoveredServiceInfo.builder()
                .peerId(runtimeData.getId())
                .nick(runtimeData.getName())
                .ip(curIp)
                .port(curPort)
                .build();
        final String json = objectMapper.writeValueAsString(curInfo);
        log.debug("✅ [MdnsRegistrationService] Создан JSON: peerId={}, nick={}, ip={}, port={}",
                runtimeData.getId(), runtimeData.getName(), curIp, curPort);
        return json;
    }

    @Override
    public void destroy() throws Exception {
        log.info("🛑 [MdnsRegistrationService] Начало остановки mDNS сервиса...");

        if (jmdns != null) {
            log.info("🔓 [MdnsRegistrationService] Закрытие JmDNS...");
            jmdns.close();
            log.info("✅ [MdnsRegistrationService] JmDNS успешно закрыт");
        } else {
            log.warn("⚠️ [MdnsRegistrationService] JmDNS уже null, пропуск закрытия");
        }

        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            log.info("⏹️ [MdnsRegistrationService] Остановка планировщика задач...");
            scheduledExecutorService.shutdown();
            log.info("✅ [MdnsRegistrationService] Планировщик задач остановлен");
        }

        final int registrationsCount = registrations.size();
        log.info("📊 [MdnsRegistrationService] Очистка {} зарегистрированных сервисов", registrationsCount);
        registrations.clear();

        log.info("✅🏁 [MdnsRegistrationService] mDNS сервис остановлен");
    }
}
