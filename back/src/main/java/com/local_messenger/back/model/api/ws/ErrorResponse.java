package com.local_messenger.back.model.api.ws;

import org.apache.logging.log4j.CloseableThreadContext.Instance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private Instance timestamp;
}
