package com.myshopnet.client;

import com.google.gson.Gson;
import com.myshopnet.AppState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatSession {
    private static final String HOST = "localhost";
    private static final int PORT = 8081;

    private final Gson gson = new Gson();

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread readerThread;
    private volatile boolean running = false;

    public void listenToIncomingMessages() {
        synchronized (this) {
            if (readerThread != null && readerThread.isAlive()) {
                return;
            }

            stop();
            try {
                socket = new Socket(HOST, PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                running = true;

                Map<String, Object> request = new HashMap<>();
                request.put("chatId", Auth.getCurrentChatId());
                out.println(gson.toJson(request));
                out.flush();

                readerThread = new Thread(() -> {
                    try {
                        String line;

                        while (running && AppState.chatActive && (line = in.readLine()) != null) {
                            try {
                                String message = line;
                                System.out.println("");
                                System.out.println(message);
                            } catch (Exception ignored) {

                            }
                        }

                    }
                    catch (IOException ignored) {
                    } finally {
                        stop();
                    }
                });
                readerThread.setDaemon(true);
                readerThread.start();
            } catch (IOException e) {
                stop();
            }
        }
    }

    public void sendMessage(String message) {
        Map<String, String> requestMap = new HashMap<>();

        requestMap.put("userId", Auth.getUsername());
        requestMap.put("message", message);
        requestMap.put("chatId", Auth.getCurrentChatId());

        out.println(Singletons.GSON.toJson(requestMap));
        out.flush();
    }

    public void stop() {
        running = false;
        try {
            if (out != null) out.close();
        } catch (Exception ignored) {
        } finally {
            out = null;
        }
        try {
            if (in != null) in.close();
        } catch (Exception ignored) {
        } finally {
            in = null;
        }
        try {
            if (socket != null) socket.close();
        } catch (Exception ignored) {
        } finally {
            socket = null;
        }
        if (readerThread != null) {
            try {
                if (readerThread.isAlive()) {
                    readerThread.interrupt();
                }
            } catch (Exception ignored) {
            } finally {
                readerThread = null;
            }
        }
    }
}
