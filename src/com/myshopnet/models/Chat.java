package com.myshopnet.models;

import com.myshopnet.auth.UserAccount;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Chat {
    private static final SimpleDateFormat TS = new SimpleDateFormat("HH:mm");
    private final String id;
    private final Map<String, UserAccount> usersInChat;
    private List<ChatMessage> messages = new ArrayList<>();
    private List<PrintWriter> writers;

    public Chat() {
        this.id = UUID.randomUUID().toString();
        this.usersInChat = new ConcurrentHashMap<>();
        this.writers = new CopyOnWriteArrayList<>();
    }

    public List<PrintWriter> getWriters() {
        return writers;
    }

    public String getId() { return id; }
    public Map<String, UserAccount> getUsersInChat() {
        return usersInChat;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }
}


