package com.myshopnet.chat;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationHub {
    private static final Map<String, CopyOnWriteArrayList<PrintWriter>> subscribers = new ConcurrentHashMap<>();

    public static void subscribe(String userId, PrintWriter writer) {
        subscribers.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(writer);
    }

    public static void unsubscribe(String userId, PrintWriter writer) {
        CopyOnWriteArrayList<PrintWriter> list = subscribers.get(userId);
        if (list != null) {
            list.remove(writer);
            if (list.isEmpty()) {
                subscribers.remove(userId);
            }
        }
    }

    public static void notifyUser(String userId, String jsonMessage) {
        CopyOnWriteArrayList<PrintWriter> list = subscribers.get(userId);
        if (list != null) {
            for (PrintWriter writer : list) {
                try {
                    writer.println(jsonMessage);
                    writer.flush();
                } catch (Exception ignored) { }
            }
        }
    }
}
