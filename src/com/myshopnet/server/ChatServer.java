package com.myshopnet.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.client.ChatMenu;
import com.myshopnet.data.Data;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.logs.Logger;
import com.myshopnet.models.Chat;
import com.myshopnet.models.ChatMessage;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private final Logger logger = Singletons.LOGGER;
    private static final int PORT = 8081;
    private final Gson gson = GsonSingleton.getInstance();
    private Map<String, List<PrintWriter>> chats;

    public Map<String, List<PrintWriter>> getChats() {
        return chats;
    }

    public void startListening() throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            chats = new HashMap<>();
            logger.log(new LogEvent(LogType.SERVER_LISTEN, "Chat Server up on " + PORT + ". Waiting for clients..."));

            while (true) {
                System.out.println("Waiting for messages to broadcast...");

                Socket socket = server.accept();

                logger.log(new LogEvent(LogType.REQUEST_RECIEVED, "Request from " + socket.getRemoteSocketAddress() + " recieved"));

                new Thread(() -> {
                    String requestData;

                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                        while (socket.isConnected() && (requestData = in.readLine()) != null) {
                            System.out.println("INSIDE CHAT SERVER: " + socket.getRemoteSocketAddress());
                            if (requestData.trim().isEmpty()) {
                                continue;
                            }

                            try {
                                JsonObject request = JsonParser.parseString(requestData).getAsJsonObject();

                                if (request.has("chatId") && !request.has("userId") && !request.has("message")) {
                                    System.out.println("Adding " + request.get("chatId").getAsString() + " to chat list");
                                    addUserToChat(request.get("chatId").getAsString(), out);
                                }
                                else {

                                    ChatMessage message = gson.fromJson(requestData, ChatMessage.class);
                                    message.setTimestamp(System.currentTimeMillis());
                                    RequestHandler.handleMessage(message);
                                }
                            } catch (Exception ex) {
                                out.println("{\"error\":\"Invalid request\"}");
                                out.flush();
                                logger.log(new LogEvent(LogType.REQUEST_RECIEVED, "Bad request: " + ex.getMessage()));
                            }
                        }


                    } catch (IOException e) {
                        logger.log(new LogEvent(LogType.SERVER_LISTEN, e.getMessage()));
                    }
                }).start();
            }
        }
    }

    private void addUserToChat(String chatId, PrintWriter out) {
        if (!chats.containsKey(chatId)) {
            chats.put(chatId, new CopyOnWriteArrayList<>());
        }
        chats.get(chatId).add(out);
    }
}
