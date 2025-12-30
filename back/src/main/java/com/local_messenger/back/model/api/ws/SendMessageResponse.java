
package com.local_messenger.back.model.api.ws;

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
@Schema(description = "Статус отправки сообщения")
public class SendMessageResponse {
    @Schema(description = "ID сообщения", example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID messageId;

    @Schema(description = "ID диалога", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID dialogId;

    @Schema(description = "Статус", example = "sent", allowableValues = {"sent", "delivered", "read"})
    private String status;
}
