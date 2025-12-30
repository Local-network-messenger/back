
package com.local_messenger.back.model.api.ws;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос истории сообщений")
public class HistoryChunkRequest {
    @Schema(description = "ID диалога", example = "550e8400-e29b-41d4-a716-446655440001", requiredMode = REQUIRED)
    private UUID dialogId;

    @Schema(description = "Лимит сообщений", example = "50", defaultValue = "50")
    @Builder.Default
    private Integer limit = 50;

    @Schema(description = "Смещение", example = "0", defaultValue = "0")
    @Builder.Default
    private Integer offset = 0;
}

