package com.myshopnet.logs;

import java.time.Instant;
import java.time.LocalDateTime;

public class LogEvent {
    private final LogType type;
    private final LocalDateTime timestamp;
    private final String message;

    public LogEvent(LogType type, String message) {
        this.type = type;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public LogType getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
}

