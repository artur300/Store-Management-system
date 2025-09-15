package com.myshopnet.server;

import com.google.gson.Gson;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationHub {
    private static final Map<String, CopyOnWriteArrayList<PrintWriter>> subscribers = new ConcurrentHashMap<>();
    private static final Gson gson = GsonSingleton.getInstance();

    public static void subscribe(String username, PrintWriter writer) {
        subscribers.computeIfAbsent(username, k -> new CopyOnWriteArrayList<>()).add(writer);
    }

    public static void unsubscribe(String username, PrintWriter writer) {
        CopyOnWriteArrayList<PrintWriter> list = subscribers.get(username);

        if (list != null) {
            list.remove(writer);
            if (list.isEmpty()) {
                subscribers.remove(username);
            }
        }
    }

    public static void notifyUser(PrintWriter writer, String message) {
        try {
            writer.println(message);
            writer.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void notifyUsers(List<PrintWriter> writers, String message) {
        if (writers != null) {
            for (PrintWriter writer : writers) {
                try {
                    writer.println(message);
                    writer.flush();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendChatRequest(String username, String chatId, PrintWriter writer) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", username);
        map.put("chatId", chatId);

        Request request = new Request("chat", gson.toJson(map));
        writer.println(gson.toJson(request));
        writer.flush();
    }
}
