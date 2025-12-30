
package com.local_messenger.back.model.api.ws;

import java.time.Instant;
import java.util.List;
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
@Schema(description = "Ответ с историей сообщений")
public class HistoryChunkResponse {
    @Schema(description = "ID диалога", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID dialogId;

    @Schema(description = "Список сообщений")
    List<DialogMessageInfo> messages;

    @Schema(description = "Есть ли еще сообщения", example = "true")
    Boolean hasMore;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Информация о сообщении в диалоге")
    public static class DialogMessageInfo {
        @Schema(description = "ID сообщения", example = "550e8400-e29b-41d4-a716-446655440002")
        private UUID id;

        @Schema(description = "ID отправителя", example = "550e8400-e29b-41d4-a716-446655440003")
        private UUID fromId;

        @Schema(description = "ID получателя", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID toId;

        @Schema(description = "Текст сообщения", example = "Привет!")
        private String text;

        @Schema(description = "Время отправки", example = "1710000000")
        private Instant timestamp;
    }
}
