package com.myshopnet.logs;

import java.time.Instant;

public class LogEvent {
    private final LogType type;
    private final Instant timestamp;
    private final String message;

    public LogEvent(LogType type, String message) {
        this.type = type;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public LogType getType() { return type; }
    public Instant getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
}

