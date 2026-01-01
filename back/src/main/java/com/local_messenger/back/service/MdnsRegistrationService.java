package com.local_messenger.back.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jmdns.JmDNS;

import com.local_messenger.back.model.mdns.MdnsDiscoveredServiceInfo;
import com.local_messenger.back.model.periferie.RuntimeData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MdnsRegistrationService implements DisposableBean {
    @Getter
    private final Map<String, MdnsDiscoveredServiceInfo> registrations = new ConcurrentHashMap<>();
    private JmDNS jmdns;
    private RuntimeData runtimeData;

    @Override
    public void destroy() throws Exception {
        if (jmdns != null) {
            jmdns.close();
        }
    }
}
