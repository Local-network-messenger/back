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
    private String peerId;
    private String nick;
    private String ip;
    private Integer port;
    private Long lastSeenTimestamp;
}
