package com.myshopnet.client;

/**
 * בסיס לכל תפריט. מוסיף hooks ברירת־מחדל לאירועי push של צ'אט,
 * כך שמי שלא צריך—לא חייב לממש כלום.
 */
public interface Menu {
    void show();

    // ==== Chat hooks (אופציונליים) ====
    default void onChatCreated(String chatId) {}
    default void onIncomingMessage(String chatId, String senderId, String message) {}
}

