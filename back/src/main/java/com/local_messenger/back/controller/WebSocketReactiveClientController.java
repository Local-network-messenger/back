package com.local_messenger.back.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.local_messenger.back.model.api.ws.HistoryChunkRequest;
import com.local_messenger.back.model.api.ws.HistoryChunkResponse;
import com.local_messenger.back.model.api.ws.SendMessageRequest;
import com.local_messenger.back.model.api.ws.SendMessageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "WebSocket API", description = "WebSocket/STOMP endpoints (описание для Swagger UI)")
public class WebSocketReactiveClientController {

    @Operation(summary = "Send message via WebSocket (STOMP)", description = "Клиент отправляет сообщение на канал /app/messages/send, получает ответ на /user/message-status.", requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = SendMessageRequest.class))), responses = {
            @ApiResponse(responseCode = "200", description = "Message status response", content = @Content(schema = @Schema(implementation = SendMessageResponse.class)))
    })
    @MessageMapping("/messages/send")
    @SendToUser("/message-status")
    public SendMessageResponse sendMessage(@Payload SendMessageRequest request, Principal principal) {
        return new SendMessageResponse();
    }

    @Operation(summary = "Get message history chunk via WebSocket (STOMP)", description = "Клиент отправляет запрос на /app/messages/history, получает chunk истории на /user/history-chunks.", requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = HistoryChunkRequest.class))), responses = {
            @ApiResponse(responseCode = "200", description = "History chunk response", content = @Content(schema = @Schema(implementation = HistoryChunkResponse.class)))
    })
    @MessageMapping("/messages/history")
    @SendToUser("/history-chunks")
    public HistoryChunkResponse getHistoryChunk(@Payload HistoryChunkRequest request, Principal principal) {
        return new HistoryChunkResponse();
    }
}
