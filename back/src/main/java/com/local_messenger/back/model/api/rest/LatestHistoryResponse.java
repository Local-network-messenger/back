package com.local_messenger.back.model.api.rest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestHistoryResponse {
    private List<DialogsInfo> dialogs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DialogsInfo {
        private String id;
        private String recepientId;

        @JsonProperty("last_message")
        private String lastMessage;
    }
}
