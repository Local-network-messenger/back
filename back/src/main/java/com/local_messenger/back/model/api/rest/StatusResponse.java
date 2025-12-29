package com.local_messenger.back.model.api.rest;

import lombok.Data;

@Data
public class StatusResponse {
    private Boolean registered;
    private String id;
    private String nick;
}
