package com.myshopnet.models;

import com.myshopnet.auth.UserAccount;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Chat {
    private static final SimpleDateFormat TS = new SimpleDateFormat("HH:mm");
    private final String id;
    private final Map<String, UserAccount> usersInChat;
    private final StringBuffer buffer;
    private List<ChatMessage> messages = new ArrayList<>();

    public Chat(String id) {
        this.id = id;
        usersInChat = new ConcurrentHashMap<>();
        buffer = new StringBuffer();
    }

    public String getId() { return id; }
    public Map<String, UserAccount> getUsersInChat() {
        return usersInChat;
    }
    public StringBuffer getBuffer() {
        return buffer;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public Set<String> getParticipantIds() {
        return usersInChat.keySet(); // userId של כל המשתתפים
    }
}


