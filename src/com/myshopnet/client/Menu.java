package com.myshopnet.client;

public interface Menu {
    void show();

    default void onChatCreated(String chatId) {}
    default void onIncomingMessage(String chatId, String senderId, String message) {}
}

