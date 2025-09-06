package com.myshopnet.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class PushClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8081;

    private final Gson gson = new Gson();

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread readerThread;
    private volatile boolean running = false;

    public void start(String userId, Consumer<JsonObject> onEvent) {
        if (running) return;
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            running = true;

            // subscribe
            JsonObject sub = new JsonObject();
            sub.addProperty("userId", userId);
            out.println(gson.toJson(sub));
            out.flush();

            readerThread = new Thread(() -> {
                try {
                    String line;
                    while (running && (line = in.readLine()) != null) {
                        try {
                            JsonObject evt = gson.fromJson(line, JsonObject.class);
                            if (onEvent != null && evt != null) onEvent.accept(evt);
                        } catch (Exception ignored) { }
                    }
                } catch (IOException ignored) {
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

    public void stop() {
        running = false;
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
        if (readerThread != null && readerThread.isAlive()) {
            try { readerThread.interrupt(); } catch (Exception ignored) {}
        }
    }
}
