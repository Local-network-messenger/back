package com.local_messenger.back.model.mdns;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MdnsDiscoveredServiceInfo {
    private Runtime runtime;
    private String serviceName;
    private String serviceType;
    private String domain;
    private String hostName;
    private String ipAddress;
    private int port;
}
