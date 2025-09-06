package com.myshopnet.chat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.logs.Logger;
import com.myshopnet.logs.LoggerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PushServer implements Runnable {
    public static final int PORT = 8081;
    private static final Logger logger = LoggerImpl.getInstance();
    private static final Gson gson = new Gson();

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            logger.log(new LogEvent(LogType.SERVER_LISTEN, "* Push server up on " + PORT + ". Waiting for subscribers..."));
            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            logger.log(new LogEvent(LogType.SERVER_LISTEN, "Push server error: " + e.getMessage()));
        }
    }

    private void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String line = in.readLine();
            if (line == null || line.isEmpty()) return;
            JsonObject json = gson.fromJson(line, JsonObject.class);
            String userId = json.get("userId").getAsString();
            // Subscribe this connection
            NotificationHub.subscribe(userId, out);

            // Keep the connection open; block the thread by reading until closed
            while (in.readLine() != null) {
                // ignore input; used only to keep connection alive
            }
        } catch (Exception ignored) {
        }
    }
}
