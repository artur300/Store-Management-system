package com.myshopnet.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.myshopnet.chat.PushServer;
import com.myshopnet.data.Data;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.logs.Logger;
import com.myshopnet.utils.Singletons;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final Logger logger = Singletons.LOGGER;
    private static final int PORT = 8080;
    private final Gson gson = new Gson();
    private BufferedReader in;
    private PrintWriter out;

    public void startListening() throws IOException {
        try {
            Thread pushThread = new Thread(new PushServer());
            pushThread.setDaemon(true);
            pushThread.start();
        } catch (Exception ignored) { }
        try (ServerSocket server = new ServerSocket(PORT)) {
            logger.log(new LogEvent(LogType.SERVER_LISTEN, "* Server up on " + PORT + ". Waiting for clients..."));
            while (true) {
                System.out.println("Waiting for clients...");

                Socket socket = server.accept();

                logger.log(new LogEvent(LogType.REQUEST_RECIEVED, "Request from " + socket.getRemoteSocketAddress() + " recieved"));

                new Thread(() -> {
                    String requestData, usernameLoggedIn = "";
                    boolean isLoggedIn = false;

                    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                        while (socket.isConnected() && (requestData = in.readLine()) != null) {
                            if (requestData.trim().isEmpty()) {
                                continue;
                            }

                            try {
                                Request request = gson.fromJson(requestData, Request.class);

                                String response = RequestHandler.handleRequest(request);

                                boolean success = JsonParser.parseString(response).getAsJsonObject().get("success").getAsBoolean();

                                if (request.getAction().equals("login")) {
                                    isLoggedIn = success;

                                    if (isLoggedIn) {
                                        usernameLoggedIn = JsonParser
                                                .parseString(JsonParser.parseString(response).getAsJsonObject().get("message").getAsString())
                                                .getAsJsonObject().get("username").getAsString();
                                    }
                                }

                                if (request.getAction().equals("logout") && isLoggedIn) {
                                    isLoggedIn = !success;

                                    if (!isLoggedIn) {
                                        logOutUser(usernameLoggedIn);
                                        usernameLoggedIn = "";
                                    }
                                }

                                out.println(response);
                                out.flush();
                            }
                            catch (Exception ex) {
                                out.println("{\"error\":\"Invalid request\"}");
                                out.flush();
                                logger.log(new LogEvent(LogType.REQUEST_RECIEVED, "Bad request: " + ex.getMessage()));
                            }
                        }

                        logger.log(new LogEvent(LogType.REQUEST_RECIEVED, "Client disconnected"));

                        if (isLoggedIn && !usernameLoggedIn.isBlank()) {
                            logOutUser(usernameLoggedIn);
                            usernameLoggedIn = "";
                        }
                    }
                    catch (IOException e) {
                        logger.log(new LogEvent(LogType.REQUEST_RECIEVED, "Client disconnected"));

                        if (isLoggedIn && !usernameLoggedIn.isBlank()) {
                            logOutUser(usernameLoggedIn);
                            usernameLoggedIn = "";
                        }

                        logger.log(new LogEvent(LogType.SERVER_LISTEN, e.getMessage()));
                    }
                }).start();
            }
        }
    }

    private void logOutUser(String username) {
        Data.getOnlineAccounts().remove(username);
    }
}
