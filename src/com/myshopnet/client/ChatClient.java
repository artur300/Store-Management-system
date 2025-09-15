package com.myshopnet.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.AppState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ChatClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    private final Gson gson = new Gson();

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread readerThread;
    private InputManager inputManager = InputManager.getInstance();
    private volatile boolean running = false;
    public static boolean gotChatNotification = false;

    public void start(Consumer<JsonObject> onRecieveChatRequest) {
        if (running) return;
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            running = true;

            Map<String, Object> request = new HashMap<>();
            request.put("action", "chatMember");
            request.put("data", Auth.getUsername());
            out.println(gson.toJson(request));
            out.flush();

            readerThread = new Thread(() -> {
                try {
                    String line;

                    while (running && (line = in.readLine()) != null) {
                        try {
                            JsonObject chatResponse = JsonParser.parseString(line).getAsJsonObject();

                            if (onRecieveChatRequest != null && chatResponse != null) {
                                onRecieveChatRequest.accept(chatResponse);
                            }
                        } catch (Exception e) {
                            if (running) {
                                System.err.println("Error handling chat message: " + e.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Chat client connection error: " + e.getMessage());
                    }
                }
                finally {
                    stop();
                }
            }, "ChatNotifyReader");
            readerThread.setDaemon(true);
            readerThread.start();
        } catch (IOException e) {
            stop();
        }
    }

    public void handleChatRequest(JsonObject chatRequest) {
        String action = chatRequest.get("action").getAsString();
        JsonObject data = JsonParser.parseString(chatRequest.get("data").getAsString()).getAsJsonObject();

        switch (action) {
            case "start": {
                startChatAsync();
                break;
            }
            case "chat": {
                String chatId = data.get("chatId").getAsString();
                String username = data.get("userId").getAsString();

                showNotificationToJoinChat(chatId, username);
                break;
            }
            case "message": {
                String userId = data.get("userId").getAsString();
                String message = data.get("message").getAsString();

                updateChatDisplay(userId, message);
                break;
            }
            case "end": {
                endChat();
                break;
            }
        }
    }

    private void endChat() {
        System.out.println("----------------- ENDED CHAT --------------------)");
        AppState.chatActive = false;
        try {
            Singletons.CHAT_SESSION.stop();
        } catch (Exception ignored) {}
        Auth.setCurrentChatId("");
    }

    public void stop() {
        running = false;
        try {
            if (out != null) out.close();
        } catch (Exception ignored) {
        } finally { out = null; }
        try {
            if (in != null) in.close();
        } catch (Exception ignored) {
        } finally { in = null; }
        try {
            if (socket != null) socket.close();
        } catch (Exception ignored) {
        } finally { socket = null; }
        if (readerThread != null) {
            try {
                if (readerThread.isAlive()) {
                    readerThread.interrupt();
                }
            } catch (Exception ignored) {
            } finally { readerThread = null; }
        }
    }

    private void updateChatDisplay(String userId, String message) {

    }

    private void showNotificationToJoinChat(String chatId, String username) {
        System.out.println("--------------- NEW CHAT ----------------");
        System.out.println();
        gotChatNotification = true;
        AppState.chatActive = true;

        System.out.println("Request from employee to enter chat");
        System.out.println("CHAT NOTFICIFICATION - PRESS ENTER A COUPLE OF TIME TO ENTER CHAT");

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) { }

        Auth.setCurrentChatId(chatId);
        startChatAsync();
    }

    public void startChat() {
        AppState.chatActive = true;
        String input;

        System.out.println("----------------- IN CHAT --------------------");
        try {
            Singletons.CHAT_SESSION.listenToIncomingMessages();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        while (AppState.chatActive && running) {
            try {
                input = inputManager.pollInput(1, TimeUnit.SECONDS);
                if (input == null) {
                    if (!AppState.chatActive || !running) {
                        break;
                    }
                    continue;
                }
                if (input == null || input.trim().isEmpty()) {
                    continue;
                }
                if (input.trim().equals("/exit")) {
                    endChat();
                    break;
                }
                sendMessage(input);
            } catch (InterruptedException e) {
                if (!AppState.chatActive || !running) {
                    Thread.currentThread().interrupt();
                    System.err.println("Chat input interrupted: " + e.getMessage());
                    break;
                }
            } catch (Exception e) {
                System.err.println("Error processing chat input: " + e.getMessage());
            }
        }

        endChat();
    }

    private void startChatAsync() {
        try {
            inputManager.clearQueue();
        } catch (Exception ignored) {}
        new Thread(() -> startChat(), "ChatLoopThread").start();
    }

    private void sendMessage(String message) {
        Map<String, String> requestMap = new HashMap<>();

        requestMap.put("userId", Auth.getUsername());
        requestMap.put("message", message);
        requestMap.put("chatId", Auth.getCurrentChatId());

        Singletons.CHAT_SESSION.sendMessage(message);
    }
}
