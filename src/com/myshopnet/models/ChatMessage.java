package com.myshopnet.models;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class ChatMessage {
    private String userId;
    private String message;
    private String chatId;
    private Long timestamp;

    public ChatMessage(String senderId, String message, String chatId) {
        this.userId = senderId;
        this.message = message;
        this.chatId = chatId;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        long timestampMillis = System.currentTimeMillis();
        Instant instant = Instant.ofEpochMilli(timestampMillis);

        return "[" + userId + "] " + " [" + LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "]: " + message;
    }
}

