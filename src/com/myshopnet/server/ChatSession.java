package com.myshopnet.server;

import java.io.OutputStream;
import java.util.List;

public class ChatSession {
    private List<OutputStream> chatOutputStreams;

    public ChatSession() { }

    public List<OutputStream> getChatOutputStreams() {
        return chatOutputStreams;
    }

    public void setChatOutputStreams(List<OutputStream> chatOutputStreams) {
        this.chatOutputStreams = chatOutputStreams;
    }
}
