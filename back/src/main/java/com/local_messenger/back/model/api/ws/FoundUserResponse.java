package com.local_messenger.back.model.api.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoundUserResponse {
    private String peerId;
    private String nick;
    private String ip;
    private String port;
}
