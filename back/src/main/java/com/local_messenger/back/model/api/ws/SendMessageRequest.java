
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
@Schema(description = "Запрос на отправку сообщения")
public class SendMessageRequest {
    @Schema(description = "ID получателя", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = REQUIRED)
    private UUID peerId;

    @Schema(description = "Текст сообщения", example = "Привет!", requiredMode = REQUIRED)
    private String text;
}
