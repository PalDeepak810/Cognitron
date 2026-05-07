package com.proc.proc.Controller.dto;

import java.time.LocalDateTime;

public record WebSocketMessage(
        String type,
        Object data,
        LocalDateTime timestamp
) {
}
