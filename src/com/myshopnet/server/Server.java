package com.myshopnet.server;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.myshopnet.data.Data;
import com.myshopnet.logs.LogEvent;
import com.myshopnet.logs.LogType;
import com.myshopnet.logs.Logger;
import com.myshopnet.models.ChatMessage;
import com.myshopnet.utils.GsonSingleton;
import com.myshopnet.utils.Singletons;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final Logger logger = Singletons.LOGGER;
    private static final int PORT = 8080;
    private final Gson gson = GsonSingleton.getInstance();
    private static Map<String, PrintWriter> allPrintWriters = new ConcurrentHashMap<>();

    public void startListening() throws IOException {
        new Thread(() -> {
                try {
                    Singletons.CHAT_SERVER.startListening();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        try (ServerSocket server = new ServerSocket(PORT)) {
            logger.log(new LogEvent(LogType.SERVER_LISTEN, "* Server up on " + PORT + ". Waiting for clients..."));

            while (true) {
                System.out.println("Waiting for clients...");

                Socket socket = server.accept();

                logger.log(new LogEvent(LogType.REQUEST_RECIEVED, "Request from " + socket.getRemoteSocketAddress() + " recieved"));

                new Thread(() -> {
                    String requestData, usernameLoggedIn = "";
                    boolean isLoggedIn = false;

                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                        while (socket.isConnected() && (requestData = in.readLine()) != null) {
                            if (requestData.trim().isEmpty()) {
                                continue;
                            }

                            String response;

                            try {
                                Request request = gson.fromJson(requestData, Request.class);

                                if (request.getAction().startsWith("chat")) {
                                    response = RequestHandler.handleChat(request, out);
                                } else {
                                    response = RequestHandler.handleRequest(request);

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
                                }

                                out.println(response);
                                out.flush();
                            } catch (Exception ex) {
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
                    } catch (IOException e) {
                        logger.log(new LogEvent(LogType.REQUEST_RECIEVED, "logout=" + usernameLoggedIn));

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
        allPrintWriters.remove(username);
    }

    public void sendMessage(List<PrintWriter> writers, ChatMessage msg) {
        try {
            for (PrintWriter writer : writers) {
                writer.println(msg.toString());
                writer.flush();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, PrintWriter> getAllPrintWriters() {
        return allPrintWriters;
    }
}
